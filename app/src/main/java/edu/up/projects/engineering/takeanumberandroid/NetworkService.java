package edu.up.projects.engineering.takeanumberandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Deploys WebSocketHandler as a service so that it run persistently throughout
 * all activities until the program is terminated
 *
 * Based on http://javatechig.com/android/android-service-example
 */
public class NetworkService extends Service {

    private static final String TAG = "Network Service";
    private static WebSocketHandler serverConnection = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                System.out.println("onStartCommand invoked");
                try
                {
                    serverConnection = new WebSocketHandler(new URI("http://192.168.1.144:8080"));

                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                serverConnection.connect();
                serverConnection.waitForReady();

                serverConnection.sendSecure("identify#tablet");
            }
        }).start();

        stopSelf();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "Service onDestroy");
    }

    public static WebSocketHandler getServerConnection()
    {
        return serverConnection;
    }
}

