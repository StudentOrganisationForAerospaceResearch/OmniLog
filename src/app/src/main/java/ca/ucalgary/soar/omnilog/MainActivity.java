package ca.ucalgary.soar.omnilog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public boolean is_logging(){
        return false;
    }

    public void startLogging(){
        return;
    }

    public void stopLogging(){
        return;
    }


}
