package ca.ucalgary.soar.omnilog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LogFile dataFile;
    boolean logging;
    Button button;
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    }

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


}
