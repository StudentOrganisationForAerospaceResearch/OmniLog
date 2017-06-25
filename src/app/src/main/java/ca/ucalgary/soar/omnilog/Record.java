package ca.ucalgary.soar.omnilog;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

class Record {
    private FileOutputStream fOut;
    private OutputStreamWriter myOutWriter;

    // With help from https://stackoverflow.com/questions/35481924/write-a-string-to-a-file
    Record(String fileName) {
        // Get the directory for the user's public pictures directory.
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

        // Once name is chosen, create file
        final File file = new File(path, fileName + ".txt");

        // Initialise file and writers
        try
        {
            fOut = new FileOutputStream(file);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("# File initialised at: " +
                    String.valueOf(DataGatheringFacade.get_timestamp())
                    + "\n# \n# Column Name (Units):" + "\n\n");

            fOut.flush();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File init failed: " + e.toString());
        }
    }


    /**
     * Format all data and print to file
     *
     * @param text Data to be written
     */
    public void writeTextToFile(String text) {
        // Write string to file
        try {
            myOutWriter.append(text);
            fOut.flush();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    /**
     * Format all data and print to file
     *
     * @param sensorData Data to be written
     */
    public void writeDataToFile(float[] sensorData) {
        String dataString = "";

        // Format all data into desired format
        for (float dataPoint:sensorData) {dataString += (dataPoint + "|");}

        // Write string to file
        try {
            myOutWriter.append(dataString);
            fOut.flush();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    /**
     * Closes the file to prevent a memory leak
     */
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
}
