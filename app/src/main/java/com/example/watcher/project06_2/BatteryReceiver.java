package com.example.watcher.project06_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class BatteryReceiver extends BroadcastReceiver {
    SharedPreferences pref;
    boolean sound, vibration, showDialog;
    int hoursFrom, hoursTo;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(context.getString(R.string.BatteryChanged))) {
            final Intent srvIntent = new Intent(context, BatteryService.class);
            srvIntent.putExtra(BatteryService.EXTRA_ACTION, intent.getAction());
            srvIntent.putExtra(BatteryService.EXTRA_LEVEL, intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
            srvIntent.putExtra(BatteryService.EXTRA_SCALE, intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
            srvIntent.putExtra(BatteryService.EXTRA_POWER, String.valueOf(intent.getExtras().get("plugged")));

//        for (String key : intent.getExtras().keySet())
//        {
//            Log.v("Bundle Debug ", key + " = \"" + intent.getExtras().get(key) + "\"");
//        }

            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder srs) {

                    // чтение настроек
                    pref = context.getSharedPreferences(context.getString(R.string.prefs_prefs), MODE_PRIVATE);
                    sound = pref.getBoolean(context.getString(R.string.prefs_sound), false);
                    vibration = pref.getBoolean(context.getString(R.string.prefs_vibration), false);
                    hoursFrom = pref.getInt(context.getString(R.string.prefs_hoursFrom), 0);
                    hoursTo = pref.getInt(context.getString(R.string.prefs_hoursTo), 0);
                    showDialog = pref.getBoolean(context.getString(R.string.prefs_showDialog), true);

                    BatteryService.LocalBinder binder = (BatteryService.LocalBinder) srs;
                    BatteryService service = binder.getService();
                    String result = service.getValue(srvIntent);

                    createNotification(context, result);

                    // диалог
                    if (result.equals(context.getString(R.string.message4)) && showDialog) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle(R.string.AlertTitle)
                                .setMessage(R.string.AlertMessage)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Toast.makeText(context, "Service crashed", Toast.LENGTH_SHORT).show();
                    context.stopService(srvIntent);
                }
            };

            context.bindService(srvIntent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    private void createNotification(Context context, String message) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.battery_icon)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification))
                .setContentText(message); // Текст уведомления

        if (sound) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (vibration) {
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
            builder.setVibrate(pattern);
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // ограничение по времени
        if (hour <= hoursFrom || hour >= hoursTo) {

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }
}
