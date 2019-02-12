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
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.p2plib2.PayLib;
import com.p2plib2.common.CommonFunctions;
import com.p2plib2.ussd.USSDController;

public class TestActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Switch chooser;
    private EditText num;
    public int simNumber = 0;
    private EditText sum;
    private Button btn;
    static String operation = Operation.SMS.toString();
    private TextView tv;
    static Handler handler;

    final String[] operators = new String[]{"MTS", "MEGAFON", "BEELINE", "TELE"};
    String number;
    String summa;
    PayLib main;
    static Context cnt;
    String operName;
    int simNum;
    String operDest;

    enum Operation {
        SMS, USSD
    }

    String regex = "[0-9]+";
    Long operationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        final Activity act = this;
        cnt = this;
        Intent intent = getIntent();
        getOperatorParam(intent.getStringExtra("operator"));
        /**Show message */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Logger.lg("text " + text);
                tv.setText(text);
            }
        };
        /**ini*/
        num = findViewById(R.id.num);
        sum = findViewById(R.id.sum);
        chooser = findViewById(R.id.switch2);
        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView2);
        if (chooser != null) {
            chooser.setOnCheckedChangeListener(this);
        }
        /**Lib ini*/
        operation = Operation.SMS.toString();
        final CallSmsResult smsResult = new CallSmsResult();
        main = new PayLib(act, cnt, smsResult, true);
        /**MTS Special*/
        final AlertDialog.Builder builderOperator = new AlertDialog.Builder(cnt);
        builderOperator.setTitle("Choose operator for destination ussd");
        builderOperator.setItems(operators, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                operDest = operators[which];
                Logger.lg("Choose " + operDest + " ");
                operationId = System.currentTimeMillis();
                main.operation(operationId, operation.toUpperCase(), act, cnt, operDest, number, summa, simNum);
                dialog.dismiss();
                dialog.cancel();
            }
        });
        /***/
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (num.getText() != null && num.getText().toString() != ""
                        && sum.getText() != null && sum.getText().toString() != "") {
                    number = num.getText().toString();
                    summa = sum.getText().toString();
                    if (number.length() == 10 && number.matches(regex)) {
                        if (operName.contains("MTS") && operation.equals(Operation.USSD.toString())) {
                            if (USSDController.isAccessiblityServicesEnable(act)) {
                                AlertDialog alertD = builderOperator.create();
                                alertD.show();
                            } else {
                                Message msg = new Message();
                                msg.obj = "Code P2P-015: Пожалуйста, разрешите приложению доступ в разделе \'Специальные возможности\' (для вызова окна перехода можно нажать кнопу <Обновить данные...>)";
                            }
                        } else {
                            Logger.lg(" number " + number + "  summa " + summa
                                    + "  " + operation  + " sim num " + simNum);
                            operationId = System.currentTimeMillis();
                            main.operation(operationId, operation.toUpperCase(), act, cnt, "empty", number, summa, simNum);
                        }
                    } else {
                        Toast.makeText(cnt, "Неверный формат номера или суммы",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(cnt, "Неверный формат номера или суммы",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getOperatorParam(String str) {
        Logger.lg("str " + str + "  " + str.trim().substring(str.indexOf("sim") + 3));
        operName = CommonFunctions.formatOperName(str.trim().substring(0, str.indexOf("sim")).trim());
        simNum = Integer.parseInt(str.trim().substring(str.indexOf("sim ") + 4).trim());
    }

    public class CallSmsResult implements com.p2plib2.CallSmsResult {
        @Override
        public void callResult(String str) {
            Logger.lg("catch " + str + " " + str.contains("-001:"));
            if (str.contains("Process ") && !str.contains("Process null")) {
                btn.setEnabled(true);
                Logger.lg(" вып  " + str);
                if (!str.toLowerCase().contains("жида") && !str.toLowerCase().contains("принят") && !str.contains("-001:")
                        && !str.toLowerCase().contains("ответ") && !str.toLowerCase().contains("к оплате")) {
                    Intent intent = new Intent(TestActivity.this, Result.class);
                    intent.putExtra("Result", formatResult(str));
                    if (str.contains("Process ") && !str.contains("Process null") ) {
                        String sy = str.toLowerCase().replaceAll("\\[", "").replaceAll("\\]", "").trim()
                                .substring(str.indexOf("process ") + 8);
//                        Logger.lg("CODE "  + " " +  sy.trim().substring(0, str.indexOf("code")));
                        try {
                            //main.clearProcess(sy);
                        } catch (Exception e) {
                            Logger.lg(" error " + sy + "  " + e.getMessage());
                        }
                    }
                    Logger.lg("fgrt");
                    startActivity(intent);
                }
            } else if(str.contains("-001:")){
                btn.setEnabled(true);
            }
        }
    }

    private String formatResult(String str) {
        Logger.lg("Before format " + str);
        String result;
        if (str.toLowerCase().contains("не дост") ||
                str.toLowerCase().contains("не про") || str.toLowerCase().contains("отказ")
                || str.toLowerCase().contains("не осущ") || str.toLowerCase().contains("не выполн")
                || str.toLowerCase().contains("не успеш") || str.toLowerCase().contains("не удал")
                || str.toLowerCase().contains("ошиб")
                || str.toLowerCase().contains("неверн")
                || str.toLowerCase().contains("не мож")
                || str.toLowerCase().contains("нельз")
                || str.toLowerCase().contains("заблокир")
                || str.toLowerCase().contains("поздн")
                || str.toLowerCase().contains("недоступ")) {
            if (str.toLowerCase().contains("ошибка отпра")) {
                result = "Error: Платеж не выполнен по причине: " + str.substring(str.indexOf("] Code") + 2);
            } else {
                result = "Error: Платеж не выполнен по причине: " + str;
            }
        } else if (!str.toLowerCase().contains("жида") && !str.toLowerCase().contains("принят") && !str.contains("-001:")) {
            result = "Сообщение о доставке:  Успешно\n" +
                    "Сумма в " + sum.getText().toString() + " переведена на номер "
                    + " " + num.getText().toString();
        } else {
            result = str;
        }
        Logger.lg("fb " + str);
        return result;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            operation = Operation.USSD.toString();
        } else {
            operation = Operation.SMS.toString();
        }
        Message msg = new Message();
        msg.obj = operation;
        handler.sendMessage(msg);
        Toast.makeText(this, "Отслеживание переключения: " + (isChecked ? "USSD" : "SMS"),
                Toast.LENGTH_SHORT).show();
    }
}
