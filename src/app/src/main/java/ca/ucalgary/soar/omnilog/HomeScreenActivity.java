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

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends Activity {
    private DataGatheringFacade dataGatherer;
    private boolean logging;
    private Button button;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while(!checkPermissions()) {
            Log.w("Warning", "Permissions not granted.");
        }

        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
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
        button.setOnClickListener(buttonListener);
        dataGatherer = new DataGatheringFacade(this);
    }


    private View.OnClickListener buttonListener = new View.OnClickListener(){
        public void onClick(View v) {
            if (!is_logging()) startLogging();
            else stopLogging();
        }
    };


    public boolean is_logging() {
        return logging;
    }

    public void startLogging(){
        dataGatherer.start();

        logging=true;
        text.setText(R.string.logging);
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.logging, null));

    }

    public void stopLogging(){
        dataGatherer.stop();

        logging=false;
        text.setText(R.string.not_logging);
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.start, null));
    }

    //Source: stackoverflow.com/a/41221852/5488468
    private boolean checkPermissions() {
        // Permissions to ask for
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        int result;

        // Give user prompt to grant permissions
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

}
