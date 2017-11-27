package com.example.watcher.project06_2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class BatteryService extends Service {
    public static final String EXTRA_LEVEL = "level";
    public static final String EXTRA_SCALE = "scale";
    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_POWER = "power";

    public BatteryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        public BatteryService getService() {
            return BatteryService.this;
        }
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        Toast.makeText(this, "Unbind successful", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    public String getValue(Intent intent) {

        SharedPreferences pref = getSharedPreferences(getString(R.string.prefs_prefs), MODE_PRIVATE);
        int minCharge = pref.getInt(getString(R.string.prefs_seekBar), 0) + 5;
        boolean low_brightness = pref.getBoolean(getString(R.string.prefs_darkTheme), false);

        String result = "";
        int level = intent.getIntExtra(EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float) scale;

        String power = intent.getStringExtra(EXTRA_POWER);

        setBackLight(0.7f);

        if (power.equals("1") && batteryPct < 100)
            result = getString(R.string.message1);

        if (power.equals("1") && batteryPct == 100)
            result = getString(R.string.message2);

        if (power.equals("0") && batteryPct > minCharge)
            result = getString(R.string.message3);

        if (power.equals("0") && batteryPct <= minCharge) {
            if (low_brightness)
                setBackLight(0.2f);
            result = getString(R.string.message4);
        }
        return result;
    }

    private void setBackLight(float value) {
        int SysBackLightValue = (int) (value * 255);
        android.provider.Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue);
    }
}
