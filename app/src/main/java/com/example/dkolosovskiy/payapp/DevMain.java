package com.example.dkolosovskiy.payapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import com.p2plib2.Logger;
import com.p2plib2.ussd.USSDController;

import java.util.HashMap;
import java.util.Map;


public class DevMain extends AppCompatActivity {

        private Button btnIni;
        private Button showSimSetting;
        private Button btnSmsSave;
        private Button btnSmsUnSave;
        private Button btnSMSNewSave;
        private Button btnSMSNewUnSave;
        private Button btnUssd;
        private Button btnUssdNew;
        private Button btnDelete;
        private TextView operLabel;
        private TextView operList;
        private static TextView textView;

        private EditText mtcNum;
        private EditText beeNum;
        private EditText teleNum;
        private EditText megaNum;
        private EditText mtcSum;
        private EditText beeSum;
        private EditText teleSum;
        private EditText megaSum;
        private Button mtcSave;
        private Button beeSave;
        private Button teleSave;
        private Button megaSave;


        HashMap<String, String> nums = new HashMap<>();
        HashMap<String, String> sums = new HashMap<>();

        String operDestination;
        com.p2plib2.PayLib main;
        static Context cnt;

        /***/
        String regex = "[0-9]+";
        Boolean sendWithSaveOutput = true;
        String curOperation = null;
        final String[] operators = new String[]{"MTS", "MEGAFON", "BEELINE", "TELE"};

        Boolean flagogek = true;
        String curOper;
        boolean curSave;
        Boolean operationFlag = false;

        public enum OperatorNames {
            BEELINE, MTS, TELE, MEGAFON
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Activity act = this;
            cnt = this;
            setContentView(R.layout.main_activity);
            /**Show message */
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String text = (String) msg.obj;
                    textView.setText(text);
                }
            };
            /**Inicialization button and etc.*/
            btnIni = findViewById(R.id.btnIni);
            showSimSetting = findViewById(R.id.btnSimParam);
            btnSmsSave = findViewById(R.id.btnSmsSave);
            btnSmsUnSave = findViewById(R.id.btnSmsUnSave);
            btnSMSNewSave = findViewById(R.id.btnSmsSaveNew);
            btnSMSNewUnSave = findViewById(R.id.btnSmsUnSaveNew);
            showSimSetting = findViewById(R.id.btnSimParam);
            btnUssd = findViewById(R.id.btnUssd);
            btnUssdNew = findViewById(R.id.btnUssdNew);
            btnDelete = findViewById(R.id.btnDelete);
            operLabel = findViewById(R.id.operLabel);
            operList = findViewById(R.id.operList);
            textView = findViewById(R.id.textView);
            mtcNum = findViewById(R.id.numMts);
            beeNum = findViewById(R.id.numBee);
            teleNum = findViewById(R.id.numTele);
            megaNum = findViewById(R.id.numMega);
            mtcSum = findViewById(R.id.sumMts);
            beeSum = findViewById(R.id.sumBee);
            teleSum = findViewById(R.id.sumTele);
            megaSum = findViewById(R.id.sumMega);
            mtcSave = findViewById(R.id.saveMTC);
            beeSave = findViewById(R.id.saveBee);
            teleSave = findViewById(R.id.saveTele);
            megaSave = findViewById(R.id.saveMega);
            textView.setScroller(new Scroller(this));
            textView.setVerticalScrollBarEnabled(true);
            textView.setMovementMethod(new ScrollingMovementMethod());
            btnDiactivate();
            /**INI lib*/
            final CallSmsResult smsResult = new CallSmsResult();
            String result = "";
            main = new com.p2plib2.PayLib(act, cnt, smsResult, true);
            /**For available sim cards**/
            /**Operator destination chooser and Ussd receiver**/
            final AlertDialog.Builder builderOperator = new AlertDialog.Builder(cnt);
            builderOperator.setTitle("Choose operator for destination ussd");
            // LayoutInflater li = LayoutInflater.from(cnt);
            mtcSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String newNum = mtcNum.getText().toString();
                    String newSum = mtcSum.getText().toString();
                    Message msg = new Message();
                    if ((newNum != null || newNum != "") && newNum.length() == 10 && newNum.matches(regex)) {
                        nums.put("MTS", newNum);
                        msg.obj = "Новый номер " + newNum;
                    } else {
                        msg.obj = "Не корректный формат номера. Пожалуйста, введите десятизначный номер (например 9876543210)";
                    }
                    com.p2plib2.Logger.lg("sum " + newSum);
                    if (newSum.matches(regex)) {
                        sums.put("MTS", newSum);
                        msg.obj = msg.obj + " новая сумма " + newSum;
                    } else if (newSum == "") {
                        sums.put("MTS", null);
                    }
                    handler.sendMessage(msg);
                }
            });
            beeSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String newNum = beeNum.getText().toString();
                    String newSum = beeSum.getText().toString();
                    Message msg = new Message();
                    if ((newNum != null || newNum != "") && newNum.length() == 10 && newNum.matches(regex)) {
                        nums.put("BEELINE", newNum);
                        msg.obj = "Новый номер " + newNum;
                    } else {
                        msg.obj = "Не корректный формат номера. Пожалуйста, введите 10-й номер";
                    }
                    if (newSum.matches(regex)) {
                        sums.put("BEELINE", newSum);
                        msg.obj = msg.obj + " новая сумма " + newSum;
                    } else if (newSum == "") {
                        sums.put("BEELINE", null);
                    }
                    handler.sendMessage(msg);
                }
            });
            teleSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String newNum = teleNum.getText().toString();
                    String newSum = teleSum.getText().toString();
                    Message msg = new Message();
                    if ((newNum != null || newNum != "") && newNum.length() == 10 && newNum.matches(regex)) {
                        nums.put("TELE", newNum);
                        msg.obj = "Новый номер " + newNum;
                    } else {
                        msg.obj = "Не корректный формат номера. Пожалуйста, введите 10-й номер";
                    }
                    if (newSum.matches(regex)) {
                        sums.put("TELE", newSum);
                        msg.obj = msg.obj + " новая сумма " + newSum;
                    } else if (newSum == "") {
                        sums.put("TELE", null);
                    }
                    handler.sendMessage(msg);
                }
            });
            megaSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String newNum = megaNum.getText().toString();
                    String newSum = megaSum.getText().toString();
                    Message msg = new Message();
                    if ((newNum != null || newNum != "") && newNum.length() == 10 && newNum.matches(regex)) {
                        nums.put("MEGAFON", newNum);
                        msg.obj = "Новый номер " + newNum;
                    } else {
                        msg.obj = "Не корректный формат номера. Пожалуйста, введите 10-й номер";

                    }
                    if (newSum.matches(regex)) {
                        sums.put("MEGAFON", newSum);
                        msg.obj = msg.obj + " новая сумма " + newSum;
                    } else if (newSum == "") {
                        sums.put("MEGAFON", null);
                    }
                    handler.sendMessage(msg);
                }
            });

            builderOperator.setItems(operators, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    operDestination = operators[which];
                    if (curOper == "ussd") {
                        com.p2plib2.Logger.lg("Choose " + operDestination + " ");
                        String num = null;
                        String sum = null;
                        if (flagogek == true) {
                            if (nums.containsKey(operDestination)) {
                                num = nums.get(operDestination);
                            }
                            if (sums.containsKey(operDestination)) {
                                sum = sums.get(operDestination);
                            }
                        }
                        main.operation(null, "USSD", act, cnt, operDestination, num, sum, null);
                        operationFlag = true;
                    } else {
                        String n = null;
                        String sum = null;
                        if (nums.containsKey(operDestination)) {
                            n = nums.get(operDestination);
                        }
                        if (sums.containsKey(operDestination)) {
                            sum = sums.get(operDestination);
                        }
                        main.operation("SMS", cnt, n, sum, curSave);
                        operationFlag = true;
                    }
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
            btnUssd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    flagogek = false;
                    main.updateData(act, cnt, smsResult, false);
                    curOper = "ussd";
                    if (USSDController.isAccessiblityServicesEnable(act)) {
                        AlertDialog alertD = builderOperator.create();
                        alertD.show();
                    } else {
                        Message msg = new Message();
                        msg.obj = "Code P2P-015: Пожалуйста, разрешите приложению доступ в разделе \'Специальные возможности\' (для вызова окна перехода можно нажать кнопу <Обновить данные...>)";
                    }
                }
            });
            btnUssdNew.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    flagogek = true;
                    main.updateData(act, cnt, smsResult, false);
                    curOper = "ussd";
                    if (USSDController.isAccessiblityServicesEnable(act)) {
                        AlertDialog alertD = builderOperator.create();
                        alertD.show();
                    } else {
                        Message msg = new Message();
                        msg.obj = "Code P2P-015: Пожалуйста, разрешите приложению доступ в разделе \'Специальные возможности\' (для вызова окна перехода можно нажать кнопу <Обновить данные...>)";
                    }
                }
            });
            /**SMS*/
            btnSmsSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    main.updateData(act, cnt, smsResult, false);
                    main.operation("SMS", act, cnt,null,true, true );
                    operationFlag = true;
                }
            });
            btnSmsUnSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    main.updateData(act, cnt, smsResult, false);
                    com.p2plib2.Logger.lg("unsave sms");
                    main.operation("SMS", act, cnt, null,false, false);
                    operationFlag = true;
                }
            });

            btnSMSNewSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    main.updateData(act, cnt, smsResult, false);
                    curOper = "sms";
                    curSave = true;
                    if (USSDController.isAccessiblityServicesEnable(act)) {
                        AlertDialog alertD = builderOperator.create();
                        alertD.show();
                    } else {
                        Message msg = new Message();
                        msg.obj = "Code P2P-015: Пожалуйста, разрешите приложению доступ в разделе \'Специальные возможности\' (для вызова окна перехода можно нажать кнопу <Обновить данные...>)";
                    }
                }
            });
            btnSMSNewUnSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    main.updateData(act, cnt, smsResult, false);
                    curOper = "sms";
                    curSave = false;
                    if (USSDController.isAccessiblityServicesEnable(act)) {
                        AlertDialog alertD = builderOperator.create();
                        alertD.show();
                    } else {
                        Message msg = new Message();
                        msg.obj = "Code P2P-015: Пожалуйста, разрешите приложению доступ в разделе \'Специальные возможности\' (для вызова окна перехода можно нажать кнопу <Обновить данные...>)";
                    }
                }
            });

            /**Button for delete*/
            btnDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    flag = true;
                    if (operationFlag) {
                        main.checkSmsDefaultApp(true, code);
                    } else {
                        Message msg = new Message();
                        msg.obj = " Пожалуйста, проведите хотя бы одну операцию оплаты";
                        handler.sendMessage(msg);
                    }
                }
            });
            /**reinizialization*/
            btnIni.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btnDiactivate();
                    main.updateData(act, cnt, smsResult, true);
                    if (ActivityCompat.checkSelfPermission(cnt, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        HashMap<Integer, String> mass = main.operatorChooser(cnt, null, 0);
                        com.p2plib2.Logger.lg("mass ");
                        String result = null;
                        com.p2plib2.Logger.lg("mass[] " + mass.size());
                        for (Map.Entry<Integer, String> sims: mass.entrySet()) {
                            result = result + " SimCard № " + sims.getKey() + " operator " + sims.getValue() + " ";
                        }
                        operList.setText(result);
                    }
                }
            });

            showSimSetting.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 1);
                }
            });
            operList.setText(result);
        }

        static Handler handler;
        Integer code = 777;
        Boolean flag = true;

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            com.p2plib2.Logger.lg("RequestCode " + requestCode);
            if (requestCode == 777) {
                boolean isDefault = resultCode == Activity.RESULT_OK;
                com.p2plib2.Logger.lg("IsDefault " + isDefault + " " + flag);
                if (isDefault && flag) {
                    main.deleteSMS(new HashMap<String, String>(), cnt);
                    flag = false;
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            com.p2plib2.Logger.lg(requestCode + " " + permissions + " " + grantResults);
        }

        public class CallSmsResult implements com.p2plib2.CallSmsResult {
            @Override
            public void callResult(String s) {
                com.p2plib2.Logger.lg(" catch  " + s);
                if (s.contains("P2P-001")) {
                    String result = "";
                    if (ActivityCompat.checkSelfPermission(cnt, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        HashMap<Integer, String> mass = main.operatorChooser(cnt, null, 0);
                        com.p2plib2.Logger.lg("dfsd " + mass.size());
                        for (Map.Entry<Integer, String> sims: mass.entrySet()) {
                            result = result + " SimCard № " + sims.getKey() + " operator " + sims.getValue() + " ";
                        }
                        operList.setText(result);
                    } else {
                        com.p2plib2.Logger.lg("not enough permissions");
                    }
                    btnActivated();
                    Logger.lg("  where view ");
                }
                if (s.contains("P2P-005")) {
                    main.checkSmsDefaultApp(false, code);
                }
                if (s.contains("P2P-002")) {
                    main.operatorChooser(cnt, "SMS", 1);
                }
                if (s.contains("P2P-013")) {
                    btnIni.setEnabled(false);
                }
                Message msg = new Message();
                msg.obj = s;
                handler.sendMessage(msg);
            }

        }

        public void btnDiactivate() {
            btnSmsSave.setEnabled(false);
            btnSmsUnSave.setEnabled(false);
            btnSMSNewSave.setEnabled(false);
            btnSMSNewUnSave.setEnabled(false);
            btnUssd.setEnabled(false);
            btnUssdNew.setEnabled(false);
            btnDelete.setEnabled(false);
            mtcSave.setEnabled(false);
            beeSave.setEnabled(false);
            teleSave.setEnabled(false);
            megaSave.setEnabled(false);
        }

        public void btnActivated() {
            btnSmsSave.setEnabled(true);
            btnSmsUnSave.setEnabled(true);
            btnSMSNewSave.setEnabled(true);
            btnSMSNewUnSave.setEnabled(true);
            btnUssd.setEnabled(true);
            btnUssdNew.setEnabled(true);
            btnDelete.setEnabled(true);
            mtcSave.setEnabled(true);
            beeSave.setEnabled(true);
            teleSave.setEnabled(true);
            megaSave.setEnabled(true);
        }
    }

