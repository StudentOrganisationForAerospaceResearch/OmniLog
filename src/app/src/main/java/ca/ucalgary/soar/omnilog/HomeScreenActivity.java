package ca.ucalgary.soar.omnilog;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import java.util.Calendar;


public class HomeScreenActivity extends Activity {
    Record dataFile;
    boolean logging;
    Button button;
    TextView text;
    Calendar c;
    DataGatherer dataGatherer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while(!checkPermissions()) {
            Log.w("Warning", "Permissions not granted.");
        }

        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        text.setText("Not Logging");
        logging = false;
        dataGatherer = new DataGatherer((SensorManager)this.getSystemService(SENSOR_SERVICE), (LocationManager)this.getSystemService(Context.LOCATION_SERVICE));

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               if(!is_logging()){
                   startLogging();
               }
               else{
                   stopLogging();
               }
            }
        });

        logging=false;
        button.setOnClickListener(buttonListner);

    }


    private View.OnClickListener buttonListner= new View.OnClickListener(){
        public void onClick(View v) {
            if (!is_logging()) startLogging();
            else stopLogging();
        }
    };


    public boolean is_logging() {
        return logging;
    }

    public void startLogging(){
        dataGatherer.startLogging();
        dataFile = new Record("log");
        dataFile.writeToFile(new float[]{1.0f, 2.0f, 3.0f});

        logging=true;
        text.setText("Logging");
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.logging, null));

    }

    public void stopLogging(){
        dataGatherer.stopLogging();
        dataFile.closeFile();
        dataFile = null;

        logging=false;
        text.setText("Not Logging");
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.start, null));
    }

    //Source: stackoverflow.com/a/41221852/5488468
    private boolean checkPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        int result;

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    public long get_timestamp(){
        return c.getTimeInMillis();
    }

}
