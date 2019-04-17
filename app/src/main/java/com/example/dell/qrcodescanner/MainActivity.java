package com.example.dell.qrcodescanner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button scanCode;
    private Button showResult;
    private Button sendResultToWhatsApp;
    private Button sendNamesToWhatsApp;
    private Button startShift;
    private Button resetApp;

    private final int MY_CAMERA_REQUEST_CODE = 100;

    private String scanResult;
    private IntentIntegrator qrScan;

    private ListView dataList;
    private ArrayAdapter arrayAdapter;

    private MyDatabaseHelper databaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrScan = new IntentIntegrator(this);

        dataList = findViewById(R.id.listView);

        scanCode = findViewById(R.id.scanCode);
        showResult = findViewById(R.id.showResult);
        sendResultToWhatsApp = findViewById(R.id.sendToWhatsapp);
        sendNamesToWhatsApp = findViewById(R.id.sendNames);
        startShift = findViewById(R.id.startNewShift);
        resetApp = findViewById(R.id.resetApp);

        databaseHelper = new MyDatabaseHelper(this);

        checkCameraPermission();

        scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initScan();
            }
        });

        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showResult();
            }
        });

        sendResultToWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              sendToWhatsApp(getResult());
            }
        });

        sendNamesToWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToWhatsApp(databaseHelper.getAllNames());
            }
        });

        startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewShift();
            }
        });

        resetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              resetApp();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkCameraPermission()
    {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }}//end onRequestPermissionsResult

    private void initScan()
    {
        qrScan.setPrompt("Scan a QR code");
        qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(ScannerActivity.class);
        qrScan.initiateScan();
    }

    private void sendToWhatsApp(ArrayList<String> data)
    {
        if (data.size()==0)
            Toast.makeText(MainActivity.this , "nothing to send !!" , Toast.LENGTH_SHORT ).show();
        else {
            ArrayListHolder listHolder = new ArrayListHolder(data);
            Intent intent = new Intent(getApplicationContext(), WhatAppMessage.class);
            intent.putExtra("items", listHolder);
            startActivity(intent);
        }
    }

    private void resetApp()
    {
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Reset the app ?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (arrayAdapter != null)
                    arrayAdapter.clear();
                databaseHelper.deleteAllData();
                databaseHelper.deleteAllNames();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
            }
        });
        adb.show();
    }

    private void startNewShift()
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Start a New Shift ?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (arrayAdapter != null)
                    arrayAdapter.clear();
                databaseHelper.deleteAllData();
            }
        });
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                scanResult = result.getContents().toString(); //  here you receive your code value
                splitData();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void splitData()
    {
        String[] holder = scanResult.split("-");
        DataModel dataModel = new DataModel(holder[0]);

        for (int index =1;index<holder.length;index++)
        {
            if (holder[index].contains("a"))
                dataModel.setA(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("b"))
                dataModel.setB(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("c"))
                dataModel.setC(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("d"))
                dataModel.setD(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("e"))
                dataModel.setE(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("f"))
                dataModel.setF(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("g"))
                dataModel.setG(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("h"))
                dataModel.setH(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("i"))
                dataModel.setI(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("j"))
                dataModel.setJ(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("k"))
                dataModel.setK(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("l"))
                dataModel.setL(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("m"))
                dataModel.setM(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("n"))
                dataModel.setN(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("o"))
                dataModel.setO(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("p"))
                dataModel.setP(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("q"))
                dataModel.setQ(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("r"))
                dataModel.setR(Integer.parseInt(holder[++index]));
            else if (holder[index].contains("s"))
                dataModel.setS(Integer.parseInt(holder[++index]));
        }
        showDataOfQRCode(dataModel);
    }

    private void showDataOfQRCode(DataModel dataModel)
    {
        ArrayList<String> listData = new ArrayList<>();
        listData.add("الأسم: " + dataModel.getName());

        if (dataModel.getA() != 0)
            listData.add(parseCode('a') + dataModel.getA() + " قطعة " );
        if (dataModel.getB() != 0)
            listData.add(parseCode('b') + dataModel.getB() + " قطعة " );
        if (dataModel.getC() != 0)
            listData.add(parseCode('c') + dataModel.getC() + " قطعة " );
        if (dataModel.getD() != 0)
            listData.add(parseCode('d') + dataModel.getD() + " قطعة " );
        if (dataModel.getE() != 0)
            listData.add(parseCode('e') + dataModel.getE() + " قطعة " );
        if (dataModel.getF() != 0)
            listData.add(parseCode('f') + dataModel.getF() + " قطعة " );
        if (dataModel.getG() != 0)
            listData.add(parseCode('g') + dataModel.getG() + " قطعة " );
        if (dataModel.getH() != 0)
            listData.add(parseCode('h') + dataModel.getH() + " قطعة " );
        if (dataModel.getI() != 0)
            listData.add(parseCode('i') + dataModel.getI() + " قطعة " );
        if (dataModel.getJ() != 0)
            listData.add(parseCode('j') + dataModel.getJ() + " قطعة " );
        if (dataModel.getK() != 0)
            listData.add(parseCode('k') + dataModel.getK() + " قطعة " );
        if (dataModel.getL() != 0)
            listData.add(parseCode('l') + dataModel.getL() + " قطعة " );
        if (dataModel.getM() != 0)
            listData.add(parseCode('m') + dataModel.getM() + " قطعة " );
        if (dataModel.getN() != 0)
            listData.add(parseCode('n') + dataModel.getN() + " قطعة " );
        if (dataModel.getO() != 0)
            listData.add(parseCode('o') + dataModel.getO() + " قطعة " );
        if (dataModel.getP() != 0)
            listData.add(parseCode('p') + dataModel.getP() + " قطعة " );
        if (dataModel.getQ() != 0)
            listData.add(parseCode('q') + dataModel.getQ() + " قطعة " );
        if (dataModel.getR() != 0)
            listData.add(parseCode('r') + dataModel.getR() + " قطعة " );
        if (dataModel.getS() != 0)
            listData.add(parseCode('s') + dataModel.getS() + " قطعة " );

        arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 , listData);
        dataList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        if (databaseHelper.addData(dataModel)) {
            if (databaseHelper.addName(dataModel.getName())){
              Toast.makeText(this, "added to database", Toast.LENGTH_SHORT).show();}
        }
        else
            Toast.makeText(this,"Scan Again !! Something went wrong" , Toast.LENGTH_SHORT).show();

    }

    private String parseCode(char code)
    {
        switch (code){
            case 'a':
                return "اطفال كبير: ";
            case 'b':
                return "اطفال صغير: ";
            case 'c':
                return "بيتى حريمى: ";
            case 'd':
                return "بلوزات: ";
            case 'e':
                return "طرح: ";
            case 'f':
                return "عبايات: ";
            case 'g':
                return "شتوى: ";
            case 'h':
                return "شنط: ";
            case 'i':
                return "احذية: ";
            case 'j':
                return "مفروشات: ";
            case 'k':
                return "بدل: ";
            case 'l':
                return "قماش وجينز: ";
            case 'm':
                return "بيتى رجالى: ";
            case 'n':
                return "قمصان: ";
            case 'o':
                return "الحريمى جديد: ";
            case 'p':
                return "الرجالى جديد: ";
            case 'q':
                return "الاخرى: ";
            case 'r':
                return "فستان سوارية: ";
            case 's':
                return "فستان الفرح: ";
        }
        return null;
    }

    private void showResult()
    {
        ArrayList<String> listData = getResult();
        arrayAdapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1 , listData);
        dataList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> getResult()
    {
        ArrayList<String> listData = new ArrayList<>();

        for (char temp = 'a' ; temp <= 's' ; temp++)
        {
            int sum = databaseHelper.getSumOfColumn(temp);
            int holder = 1;

            switch(temp)
            {
                case 'a':
                    holder = 50;
                    break;
                case 'b':
                    holder = 50;
                    break;
                case 'c':
                    holder = 30;
                    break;
                case 'd':
                    holder = 30;
                    break;
                case 'e':
                    holder = 50;
                    break;
                case 'f':
                    holder = 15;
                    break;
                case 'g':
                    holder = 15;
                    break;
                case 'h':
                    holder = 15;
                    break;
                case 'i':
                    holder = 20;
                    break;
                case 'j':
                    holder = 20;
                    break;
                case 'k':
                    holder = 10;
                    break;
                case 'l':
                    holder = 15;
                    break;
                case 'm':
                    holder = 20;
                    break;
                case 'n':
                    holder = 20;
                    break;
                case 'o':
                    holder = 20;
                    break;
                case 'p':
                    holder = 20;
                    break;
                case 'q':
                    holder = 20;
                    break;
                case 'r':
                    holder = 20;
                    break;
                case 's':
                    holder = 1;
                    break;
            }
            int result = 0;
            if (sum != 0)
            {
                double num = sum;
                num /= holder;
                if ((num % 2 != 0) && (num % 2 != 1))
                {
                    result = (int) num;
                    result += 1;
                }
                else
                    result = (int) num;

                listData.add(parseCode(temp) + " " + result + " شوال ");
            }
        }
        return listData;
    }

}
