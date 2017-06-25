package ca.ucalgary.soar.omnilog;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Colt on 24/06/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener smsListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        //This gathers up the data received in pdu format and constructs an sms message from it
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        //The loop is in case more than one message was received before the intent was fullfilled
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();

            //true is a placeholder for comparing sender number
            if (true) {
                String messageBody = smsMessage.getMessageBody();
                if(messageBody.toLowerCase().equals("status"))
                    //This will call the statusRequestReceived in the HomeScreenActivity
                    smsListener.statusRequestReceived();
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        smsListener = listener;
    }
}