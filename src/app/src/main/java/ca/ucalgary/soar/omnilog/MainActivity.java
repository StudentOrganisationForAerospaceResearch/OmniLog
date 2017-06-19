package ca.ucalgary.soar.omnilog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    LogFile dataFile;
    boolean logging;
    Button button;
    TextView text;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while(!checkPermissions()) {
            Log.e("Exception", "Permissions not granted.");
        }

        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        text.setText("Not Logging");
        logging = false;

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


    public boolean is_logging(){
        return logging;
    }

    public void startLogging(){
        dataFile = new LogFile("log");
        dataFile.writeToFile(new double[]{1.0, 2.0, 3.0});
        logging=true;
        text.setText("Logging");
    }

    public void stopLogging(){
        dataFile.closeFile();
        dataFile = null;
        logging=false;
        text.setText("Not Logging");
    }

    //Source: stackoverflow.com/a/41221852/5488468
    private boolean checkPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
