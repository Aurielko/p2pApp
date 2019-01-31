package com.example.dkolosovskiy.payapp;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.p2plib2.PayLib;
import com.p2plib2.common.CommonFunctions;

import java.util.HashMap;
import java.util.Map;

public class SimChooser extends ListActivity {

    private ArrayAdapter<String> mAdapter;
    PayLib main;
    com.p2plib2.Simple.PayLib simpleMain;
    Button butRet;

    static Context cnt;
    Activity act;
    boolean permissionAll;
    boolean ini = false;

    public Boolean checkPermissions() {
        return (ActivityCompat.checkSelfPermission(cnt, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(cnt, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(cnt, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(cnt, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(cnt, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(cnt, Manifest.permission.MODIFY_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        cnt = this;
        main = new PayLib();
        simpleMain = new com.p2plib2.Simple.PayLib();
        if (checkPermissions()) {
            permissionAll = true;
            ini();
        } else {
            setContentView(R.layout.sim_chooser);
            mAdapter = new ArrayAdapter<String>(cnt,
                    android.R.layout.simple_list_item_1, new String[]{"Идет обновление данных"});
            setListAdapter(mAdapter);
            butRet = findViewById(R.id.butRet);
            butRet.setVisibility(View.VISIBLE);
            CommonFunctions.permissionCheck(cnt, act);
            butRet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommonFunctions.permissionCheck(cnt, act);
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Logger.lg("requestCode " + requestCode + " " + permissions + " " + grantResults);
        if (requestCode == 200 && !permissionAll) {
            permissionAll = true;
            for (int i = 0; i < grantResults.length - 1; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED && !permissions[i].equals("android.permission.BIND_ACCESSIBILITY_SERVICE")) {
                    Logger.lg("false " + permissions[i]);
                    permissionAll = false;
                }
            }
            if (permissionAll) {
                ini();
            }
        }
    }


    public void ini() {
        if (!ini) {
            butRet.setVisibility(View.GONE);
            butRet.setText("");
            Logger.lg("initiation");
            final CallSmsResult smsResult = new CallSmsResult();
            main.updateData(act, cnt, smsResult, true);
            HashMap<Integer, String> mapSim = simpleMain.operatorChooser(cnt, act);
            HashMap<Integer, String> map2 = new HashMap();
            for (Map.Entry<Integer, String> str : mapSim.entrySet()) {
                if (!str.getValue().toLowerCase().contains("сигн")) {
                    map2.put(str.getKey(), str.getValue());
                }
            }
            String[] sims = new String[map2.size()];
            int i = 0;
            for (Map.Entry<Integer, String> str : map2.entrySet()) {
                if (!str.getValue().toLowerCase().contains("сигн")) {
                    sims[i] = str.getValue() + " sim " + str.getKey();
                    Logger.lg(sims[i]);
                    i++;
                }
            }
            Logger.lg("size " + sims.length);
            mAdapter = new ArrayAdapter<String>(cnt,
                    android.R.layout.simple_list_item_1, sims);
            setListAdapter(mAdapter);
            setContentView(R.layout.sim_chooser);
            ini = true;
            Logger.lg("ini " + ini);
        }
    }

    public class CallSmsResult implements com.p2plib2.CallSmsResult {
        @Override
        public void callResult(String s) {
            if (s.contains("P2P-001") && permissionAll) {
                ini();
//            Message msg = new Message();
//            msg.obj = s;
//            handler.sendMessage(msg);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(SimChooser.this, TestActivity.class);
        intent.putExtra("operator", l.getItemAtPosition(position).toString());
        startActivity(intent);
    }

}
