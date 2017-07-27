package ca.ucalgary.soar.omnilog;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.content.Context;
import android.widget.Toast;

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

        //Listens for incoming SMS
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            //If the smsReceiver found its status request (look at SmsReceiver for more details
            public void statusRequestReceived(String sender) {

                //Log the fact that the status request was received
                Log.d("StatusRequestRecieved", "Received");
                //Send a text back regarding the status
                //Note that the first parameter, the phone number should be replaced with the senders number, right now it is sending the status to itself for testing purposes
                sendSMS(sender, dataGatherer.status());
                //Toast.makeText(getBaseContext(), dataGatherer.status(), Toast.LENGTH_SHORT).show(); //This was used as a quick result for testing purposes
            }
        });

        while(!checkPermissions()) {
            Log.w("Warning", "Permissions not granted.");
        }

        button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.textView);
        text.setText("Not Logging");
        logging = false;
        dataGatherer = new DataGatherer((SensorManager)this.getSystemService(SENSOR_SERVICE), (LocationManager)this.getSystemService(Context.LOCATION_SERVICE), this);

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
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
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

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        //Constructs two pendingIntents, one to respond when the sms is sent the other when it is delivered
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                //The intent comes with a result code describing what happened during the operation, from that we construct cases
                //We can replace the toast with something more substantial, say logging, but for testing this is the easiest I believe
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT)); //Explains what intent that action resonds to

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED)); //Explains what intent that action resonds to

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); //Send the message with the two intents, awaiting results of the send.
    }

}
