package ca.ucalgary.soar.omnilog;

import android.content.Context;

public class DataGatheringFacade {
    private Context context;
    private Record dataFile;
    private DataGatherer dataGatherer;
    private ParachuteController parachutes;



    public DataGatheringFacade(Context context) {
        this.dataFile = new Record("log");
        this.context = context;
        dataGatherer = new DataGatherer(this.context, dataFile);
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
}
