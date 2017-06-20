package ca.ucalgary.soar.omnilog;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Colt on 19/06/2017.
 */

public class Record {
    private FileOutputStream fOut;
    private OutputStreamWriter myOutWriter;
    private String[] sensors;
    private float[] data;

    // With help from https://stackoverflow.com/questions/35481924/write-a-string-to-a-file
    Record(String fileName, String[] Sensors) {
        // Get the directory for the user's public pictures directory.
        sensors = Sensors;
        data = new float[sensors.length];
        System.out.println("Hello");
        final File path = Environment.getExternalStoragePublicDirectory("/OmniLog");

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            if (!path.mkdirs()) {
                Log.e("Exception", "Directory not created");
            }
        }


        final File file = new File(path, fileName + ".txt");

        try
        {
            fOut = new FileOutputStream(file);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("# File initialised at: " + "# \n# Column Name (Units):");

            fOut.flush();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File init failed: " + e.toString());
        }
    }

    public void writeToFile() {
        String dataString = "";

        for (float dataPoint:data) {
            dataString += (dataPoint + "|");
        }

        try {
            myOutWriter.append(dataString);
            fOut.flush();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void closeFile() {
        try
        {
            myOutWriter.close();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File closure failed: " + e.toString());
        }

    }

    public boolean update(int sensor, float value) {
        return false;
    }
}
