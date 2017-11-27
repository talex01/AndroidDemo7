package com.example.watcher.project06_2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    CheckBox sound, vibration, showDialog, darkTheme;
    SeekBar seekBar;
    Spinner hoursFrom, hoursTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        final TextView valueLowTextView = (TextView) findViewById(R.id.value_low);

        valueLowTextView.setText(seekBar.getProgress() + 5 + "%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueLowTextView.setText(seekBar.getProgress() + 5 + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hoursFrom = (Spinner) findViewById(R.id.hoursFrom);
        hoursTo = (Spinner) findViewById(R.id.hoursTo);
        sound = (CheckBox) findViewById(R.id.sound);
        vibration = (CheckBox) findViewById(R.id.vibro);
        showDialog = (CheckBox) findViewById(R.id.showDialog);
        darkTheme = (CheckBox) findViewById(R.id.darkTheme);

    }

    @Override
    protected void onPause() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.prefs_prefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        pref.edit().clear().commit();
        editor.putBoolean(getString(R.string.prefs_sound), sound.isChecked());
        editor.putBoolean(getString(R.string.prefs_vibration), vibration.isChecked());
        editor.putBoolean(getString(R.string.prefs_showDialog), showDialog.isChecked());
        editor.putBoolean(getString(R.string.prefs_darkTheme), darkTheme.isChecked());
        editor.putInt(getString(R.string.prefs_hoursFrom), hoursFrom.getSelectedItemPosition());
        editor.putInt(getString(R.string.prefs_hoursTo), hoursTo.getSelectedItemPosition());
        editor.putInt(getString(R.string.prefs_seekBar), seekBar.getProgress());
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.prefs_prefs), MODE_PRIVATE);
        sound.setChecked(pref.getBoolean(getString(R.string.prefs_sound), false));
        vibration.setChecked(pref.getBoolean(getString(R.string.prefs_vibration), false));
        hoursFrom.setSelection(pref.getInt(getString(R.string.prefs_hoursFrom), 1));
        hoursTo.setSelection(pref.getInt(getString(R.string.prefs_hoursTo), 1));
        seekBar.setProgress(pref.getInt(getString(R.string.prefs_seekBar), 0));
        showDialog.setChecked(pref.getBoolean(getString(R.string.prefs_showDialog), true));
        darkTheme.setChecked(pref.getBoolean(getString(R.string.prefs_darkTheme), false));
        super.onResume();
    }
}
