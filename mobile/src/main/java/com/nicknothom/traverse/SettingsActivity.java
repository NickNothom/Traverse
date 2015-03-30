package com.nicknothom.traverse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by nick on 2/3/15.
 */
public class SettingsActivity extends Activity {

    private Switch mySwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mySwitch = (Switch) findViewById(R.id.highContrastSwitch);

        if (getPreferences("UITheme").equals("dark")) {
            mySwitch.setChecked(true);
        }
        else {
            mySwitch.setChecked(false);
        }

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("UITheme", "dark").commit();
                }
                else{
                    Toast.makeText(getApplicationContext(), "OFF", Toast.LENGTH_SHORT).show();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("UITheme", "light").commit();
                }
            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            //switchStatus.setText("Switch is currently ON");
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("UITheme", "dark").commit();
        }
        else {
            //switchStatus.setText("Switch is currently OFF");
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("UITheme", "light").commit();

        }

    }


    //Returns a preference where datatype is the ID of the preference.
    public String getPreferences(String datatype) {
        String value = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(datatype, "defaultStringIfNothingFound");
        return value;
    }

    //Heads back to the main activity
    public void exitSettings(View view){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
    }
}
