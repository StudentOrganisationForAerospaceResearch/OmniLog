package ca.ucalgary.soar.omnilog;

import android.content.Context;

import java.util.Calendar;

public class DataGatheringFacade {
    Context context;
    Record dataFile;
    DataGatherer dataGatherer;
    ParachuteController parachutes;
    static Calendar c;


    public DataGatheringFacade(Context context) {
        this.context = context;
        dataGatherer = new DataGatherer(this.context, new Record("log"));
        parachutes = new ParachuteController();
    }

    public void start() {
        dataGatherer.startLogging();

        dataFile.writeDataToFile(new float[]{1.0f, 2.0f, 3.0f});

    }

    public void stop() {
        dataGatherer.stopLogging();
    }

    public void newFile(String fileName) {
        dataFile.closeFile();
        dataFile = new Record(fileName);
    }

    public static long get_timestamp(){
        return c.getTimeInMillis();
    }
}
