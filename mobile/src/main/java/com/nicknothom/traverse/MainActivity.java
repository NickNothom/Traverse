package com.nicknothom.traverse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
    //Sensor Definitions
    private SensorManager mSensorManager;
    private Sensor mCompass;
    private TextView mTextView;

    private NotificationReceiver nReceiver;


    //On Creation
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processStopService("MyServiceTag");

        //Notification Listener Service
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);

        //Grab layout elements
        //Background
        LinearLayout bg = (LinearLayout) findViewById(R.id.root);

        //Top Bar
        ImageButton btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        ImageButton btnStop = (ImageButton) findViewById(R.id.btnStop);
        ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        ImageButton btnNext = (ImageButton) findViewById(R.id.btnNext);
        ImageButton btnMediaApp = (ImageButton) findViewById(R.id.btnMediaApp);

        //Left Panel
        ImageButton btnUL = (ImageButton) findViewById(R.id.btnUL);
        ImageButton btnUR = (ImageButton) findViewById(R.id.btnUR);
        ImageButton btnBL = (ImageButton) findViewById(R.id.btnBL);
        ImageButton btnBR = (ImageButton) findViewById(R.id.btnBR);

        TextView lblLastText = (TextView) findViewById(R.id.lblLastText);
        TextView lblLastCall = (TextView) findViewById(R.id.lblLastCall);

        LinearLayout pnlGPS = (LinearLayout) findViewById(R.id.pnlGPS);

        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);

        Button btnRecord = (Button) findViewById(R.id.btnRecord);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (getPreferences("UITheme").equals("dark")) {
            bg.setBackgroundResource(R.color.black);
            btnPrevious.setBackgroundResource(R.color.darkGrey);
            btnStop.setBackgroundResource(R.color.darkGrey);
            btnPlay.setBackgroundResource(R.color.darkGrey);
            btnNext.setBackgroundResource(R.color.darkGrey);
            btnMediaApp.setBackgroundResource(R.color.darkGrey);

            btnUL.setBackgroundResource(R.color.darkGrey);
            btnUR.setBackgroundResource(R.color.darkGrey);
            btnBL.setBackgroundResource(R.color.darkGrey);
            btnBR.setBackgroundResource(R.color.darkGrey);

            lblLastText.setBackgroundResource(R.color.darkGrey);
            lblLastCall.setBackgroundResource(R.color.darkGrey);

            pnlGPS.setBackgroundResource(R.color.darkGrey);

            btnSettings.setBackgroundResource(R.color.black);

            btnRecord.setBackgroundResource(R.color.black);
        }

    }

    //Onclick Methods:
    public void openMusic (View view){
        launchApp("com.google.android.music");
    }
    public void openMaps(View view){
        launchApp("com.google.android.apps.maps");
    }
    public void openMessages (View view){
        launchApp("com.textra");
    }
    public void openPhone(View view){
        launchApp("com.google.android.dialer");
    }
    public void openCamera(View view){
        launchApp("com.google.android.GoogleCamera");
    }
    public void mediaPrevious (View view){
        mediaController("previous");
    }
    public void mediaStop (View view){
        mediaController("stop");
    }
    public void mediaPlay (View view){ mediaController("play"); }
    public void mediaNext (View view){
        mediaController("next");
    }
    public void openSettings (View view){
        settings();
    }

    //External Application Launching Method
    public void launchApp(String packagename){
        //Open app
        Intent i = new Intent();
        PackageManager manager = getPackageManager();
        i = manager.getLaunchIntentForPackage(packagename);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);

        //Open back button
        Intent intent = new Intent(MainActivity.this, PopupService.class);
        startService(intent);
    }

    //Open Settings
    public void settings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.this.startActivity(intent);
    }

    //Media Controls (pass "play, pause, stop, next, previous, etc..")
    public void mediaController(String command){
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", command);
        sendBroadcast(i);
    }

    //Stop and reset floating button
    private void processStopService(final String tag) {
        Intent intent = new Intent(getApplicationContext(), PopupService.class);
        intent.addCategory(tag);
        stopService(intent);
    }

    // The following method is required by the SensorEventListener interface;
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Hook this event to process updates;
    public void onSensorChanged(SensorEvent event) {
        //  float pitch = event.values[1];
        //  float roll = event.values[2];
        float azimuth = Math.round(event.values[0]);
        TextView txtHeading = (TextView) findViewById(R.id.lblHeading);
        txtHeading.setText("Heading: " + Float.toString(azimuth) + "°");
    }

    //On Pause
    @Override
    protected void onPause() {
        // Unregister the listener on the onPause() event to preserve battery life;
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    //On Resume
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    //Returns a preference where datatype is the ID of the preference.
    public String getPreferences(String datatype) {
        String value = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(datatype, "defaultStringIfNothingFound");
        return value;
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            //Toast.makeText(getApplicationContext(), intent.getStringExtra("notification_event"), Toast.LENGTH_SHORT).show();
            //txtView.setText(temp);
        }
    }


}
