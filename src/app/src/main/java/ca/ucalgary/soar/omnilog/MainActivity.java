package ca.ucalgary.soar.omnilog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    boolean logging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               if(is_logging()){
                   startLogging();
                   logging=true;
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
        logging=true;
    }

    public void stopLogging(){
        logging=false;
    }


}
