package com.example.dkolosovskiy.payapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.p2plib2.common.CommonFunctions;
import com.p2plib2.ussd.USSDController;

public class PermissionsChecker extends Activity {
    Button butPermission;
    static Context cnt;
    Activity act;
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
        setContentView(R.layout.permission_explain);
        butPermission = findViewById(R.id.butPro);
        if (checkPermissions() && USSDController.isAccessiblityServicesEnable(cnt)) {
            Intent intent = new Intent(PermissionsChecker.this, SimChooser.class);
            startActivity(intent);
        } else {
            butPermission.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommonFunctions.permissionCheck(cnt, act);
                }
            });
        }
    }

    boolean permissionAll;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Logger.lg("requestCode " + requestCode + " " + permissionAll + " " + USSDController.verifyAccesibilityAccess(act));
        if (requestCode == 200 && !permissionAll) {
            permissionAll = true;
            for (int i = 0; i < grantResults.length - 1; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED && !permissions[i].equals("android.permission.BIND_ACCESSIBILITY_SERVICE")) {
                    Logger.lg("false " + permissions[i]);
                    permissionAll = false;
                }
            }
        } else if (permissionAll && USSDController.isAccessiblityServicesEnable(cnt)) {
            Logger.lg("ini");
            ini=true;
            Intent intent = new Intent(PermissionsChecker.this, SimChooser.class);
            startActivity(intent);
        }
    }
}
