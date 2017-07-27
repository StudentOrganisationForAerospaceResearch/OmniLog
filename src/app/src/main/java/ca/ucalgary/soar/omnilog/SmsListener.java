package ca.ucalgary.soar.omnilog;

/**
 * Created by Colt on 24/06/2017.
 */

//An interface used to allow the HomeScreenActivity to send a text message as it has access to the dataGatherer
public interface SmsListener {
    public void statusRequestReceived(String sender);
}
