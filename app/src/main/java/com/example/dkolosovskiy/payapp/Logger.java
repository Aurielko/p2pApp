package com.example.dkolosovskiy.payapp;

import android.util.Log;

public class Logger {
    public static void lg(String s){
        Log.e("DebugCat: ", s);
        System.out.println("DebugCat: " + s);
    }
}
