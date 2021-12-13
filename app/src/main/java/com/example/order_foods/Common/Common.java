package com.example.order_foods.Common;

import android.content.Context;
import android.icu.util.LocaleData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import com.example.order_foods.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static User currentUer;
    public static String DELETE = "DELETE";

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public  static final String INTENT_FOOD_ID = "FoodId";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "Placed";
        } else if(status.equals("1")){
            return "On my way";
        }
        else {
            return "Shipperd";
        }
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null) {
                for(int i=0; i<info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

}
