package ca.ucalgary.soar.omnilog;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    boolean logging;
    Button button;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        stopLogging();


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
        logging=true;
        text.setText("Logging");
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.logging, null));

    }

    public void stopLogging(){
        logging=false;
        text.setText("Not Logging");
        button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.start, null));
    }


}
