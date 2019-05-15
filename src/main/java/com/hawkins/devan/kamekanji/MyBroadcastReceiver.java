package com.hawkins.devan.kamekanji;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hawkins on 1/9/19.
 */

public  class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String answer = intent.getStringExtra("answer");
        try {

        Class c = Class.forName("MainActivity");
        Object o = c.newInstance();

        if (answer.equals("know")) {
            //Invoke "know" method
            Method m = c.getDeclaredMethod("newCardKnow", null);
            m.setAccessible(true);
            m.invoke(o, null);

            //test
            Toast.makeText(context, "Good to know", Toast.LENGTH_SHORT).show();

        } else if (answer.equals("nope")) {
            //Invoke "nope" method
            Method m = c.getDeclaredMethod("newCardNope", null);
            m.setAccessible(true);
            m.invoke(o, null);

            //test
            Toast.makeText(context, "Good to 'no'", Toast.LENGTH_SHORT).show();
        }

        //Close notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);

    }catch(ClassNotFoundException e){
        e.printStackTrace();
    }catch (InstantiationException f){
        f.printStackTrace();
    }catch(IllegalAccessException g){
        g.printStackTrace();
    }catch (NoSuchMethodException h){
        h.printStackTrace();
    }catch (InvocationTargetException i){
        i.printStackTrace();
    }

        intent = new Intent(context, MainActivity.class);
        context.startService(intent);
    }


}
