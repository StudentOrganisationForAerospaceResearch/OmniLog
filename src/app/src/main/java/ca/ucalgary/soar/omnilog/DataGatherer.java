package ca.ucalgary.soar.omnilog;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

public class DataGatherer implements SensorEventListener, LocationListener {
    private Thread thread;
    private Context context;
    private SensorManager sm;
    private LocationManager lm;
    private Record record;
    private int gpsIndex;
    private String[] sensors;
    private float[] data;
    private static final int recordDelay = 10;
    private int[] indexMap = new int[sensorsAvailable.length];

    //This is a preconstructed list denoting the sensors we actually care to listen for, false represents those we don't such as heart rate etc.
    //                                  accel, mag,       ,gyro,light, press,     , proxi, grav, LA,  rota, humid, temp,                      sigmo                geo-rot,                                                 station,motion
    static boolean[] sensorsAvailable = {true, true, false, true, true, true, false, false, true, true, true, true, true, false, false, false, false, false, false, true,};
    //                                    1     2      3     4     5     6     7      8      9    10    11    12    13    14     15     16     17     18     19     20


    public DataGatherer(Context context, Record recorder) {
        this.context = context;
        this.record = recorder;
        sm = (SensorManager)this.context.getSystemService(SENSOR_SERVICE);
        lm = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);

        int numFields = getSensorAvailability(sm);
        numFields += 3;//Increase it by 3 because we would like to add the gps Latitude, Longitude, and Altitude
        sensors = new String[numFields];

        applySensorNames();


        //Construct the data array that will contain the data for each field we have
        data = new float[sensors.length];
        //Runs some tests, you can check them out in the log, it will display the sensorsAvailable after crossing out the ones we don't have
        //And the header of the written file (The sensor fields in the order we will right them)
        testSensorsAvailable();
        testSensorString(sensors);
        testMapTo();
    }

    public void startLogging() {
        Sensor s;

        //Loop through the available sensors and start listening for them at the fastest rate possible
        for (int i = 1; i <= sensorsAvailable.length; i++) {
            s = sm.getDefaultSensor(i);
            sm.registerListener(this, s, SENSOR_DELAY_FASTEST);
        }

        //Attempt to listen for the gps if it is available
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        } catch (SecurityException e) {
            Log.e("Error", "Permissions error for GPS or Network");
        }

        //Set logging to true and open up a thread that will continuously write the sensor readings until logging is false (when logging is stopped)
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(recordDelay);
                        record.writeDataToFile(data);

                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        record.writeTextToFile("Started collecting data at: " + String.valueOf(System.currentTimeMillis()));
        thread.start();
    }

    /**
     * Stop all associated logging processes (thread and event listeners)
     */
    public void stopLogging() {
        thread.interrupt();
        record.writeTextToFile("Stopped collecting data at: " + String.valueOf(System.currentTimeMillis()));
    }

    private void testSensorsAvailable() {
        for (int i = 0; i < sensorsAvailable.length; i++) {
            if (sensorsAvailable[i])
                Log.d("SensorsAvailable", Integer.toString(i));
        }
    }

    private void testSensorString(String[] s) {
        for (int i = 0; i < s.length; i++) {
            Log.d("SensorString", s[i]);
        }
    }

    private void testMapTo() {
        for (int i = 0; i < indexMap.length; i++) {
            Log.d("SensorMap", Integer.toString(indexMap[i]));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int i = indexMap[event.sensor.getType() - 1];
        data[i] = event.values[0];

        // Check for sensors that return values along three axes
        if (i == 1 || i == 2 || i == 3 || i == 4 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16 || i == 20) {
            data[i + 1] = event.values[1];
            data[i + 2] = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        data[gpsIndex] = (float) location.getLatitude();
        data[gpsIndex + 1] = (float) location.getLongitude();
        data[gpsIndex + 2] = (float) location.getAltitude();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private void applySensorNames() {
        int sensorCount = 0;
        //Run through the new list of available sensors (with the ones we don't have crossed out)
        for (int i = 0; i < sensorsAvailable.length; i++) {
            //If the sensor is not available
            if (!sensorsAvailable[i]) continue;

            //If it is we will append the field name's associated with the sensor to our sensor field list
            //Count references how "full" our sensor field array is, and increments by the number of fields added to i

            switch (i) {
                case 0:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Accelerometer_X";
                    sensors[sensorCount + 1] = "Accelerometer_Y";
                    sensors[sensorCount + 2] = "Accelerometer_Z";
                    sensorCount += 3;
                    break;
                case 1:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Magnetic_Field_X";
                    sensors[sensorCount + 1] = "Magnetic_Field_Y";
                    sensors[sensorCount + 2] = "Magnetic_Field_Z";
                    sensorCount += 3;
                    break;
                case 2:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Orientation_Azimuth";
                    sensors[sensorCount + 1] = "Orientation_Pitch";
                    sensors[sensorCount + 2] = "Orientation_Roll";
                    sensorCount += 3;
                    break;
                case 3:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Gyroscope_X";
                    sensors[sensorCount + 1] = "Gyroscope_Y";
                    sensors[sensorCount + 2] = "Gyroscope_Z";
                    sensorCount += 3;
                    break;
                case 4:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Light(lx)";
                    sensorCount++;
                    break;
                case 5:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Pressure(hPa)";
                    sensorCount++;
                    break;
                case 6:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Temperature(C)";
                    sensorCount++;
                    break;
                case 7:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Proximity(cm)";
                    sensorCount++;
                    break;
                case 8:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Gravity_X";
                    sensors[sensorCount + 1] = "Gravity_Y";
                    sensors[sensorCount + 2] = "Gravity_Z";
                    sensorCount += 3;
                    break;
                case 9:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "LinAcc_X";
                    sensors[sensorCount + 1] = "LinAcc_Y";
                    sensors[sensorCount + 2] = "LinAcc_Z";
                    sensorCount += 3;
                    break;
                case 10:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Rotation_X";
                    sensors[sensorCount + 1] = "Rotation_Y";
                    sensors[sensorCount + 2] = "Rotation_Z";
                    sensorCount += 3;
                    break;
                case 11:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Humidity(%)";
                    sensorCount++;
                    break;
                case 12:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "AmbTemp(C)";
                    sensorCount++;
                    break;
                case 13:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "MagFieldUncal_X";
                    sensors[sensorCount + 1] = "MagFieldUncal_Y";
                    sensors[sensorCount + 2] = "MagFieldUncal_Z";
                    sensorCount += 3;
                    break;
                case 14:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "GameRotateVector_X";
                    sensors[sensorCount + 1] = "GameRotateVector_Y";
                    sensors[sensorCount + 2] = "GameRotateVector_Z";
                    sensorCount += 3;
                    break;
                case 15:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "GyroUncal_X";
                    sensors[sensorCount + 1] = "GyroUncal_Y";
                    sensors[sensorCount + 2] = "GyroUncal_Z";
                    sensorCount += 3;
                    break;
                case 16:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Sigmo";
                    sensorCount++;
                    break;
                case 17:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "Steps";
                    sensorCount++;
                    break;
                case 18:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "StepCount";
                    sensorCount++;
                    break;
                case 19:
                    indexMap[i] = sensorCount;
                    sensors[sensorCount] = "GeomagRotation_X";
                    sensors[sensorCount + 1] = "GeomagRotation_Y";
                    sensors[sensorCount + 2] = "GeomagRotation_Z";
                    sensorCount += 3;
                    break;
            }
        }

        //Attempt to add the gps fields if it is available
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                sensors[sensorCount] = "GPS_Latitude";
                sensors[sensorCount + 1] = "GPS_Longitude";
                sensors[sensorCount + 2] = "GPS_Altitude";
                gpsIndex = sensorCount;
            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                sensors[sensorCount] = "Network_Latitude";
                sensors[sensorCount + 1] = "Network_Longitude";
                sensors[sensorCount + 2] = "Network_Altitude";
                gpsIndex = sensorCount;
            }
        } catch (SecurityException e) {
            Log.e("Error", "Security exception when checking GPS");
        }
    }

    private static int getSensorAvailability(SensorManager manager) {
        //Variable used to count the number of sensor "fields" actually available on the device
        //Thus it only increments when the device actually has a the sensor we are looking for and will increment by the number of fields
        //The sensor will return (ie, humidity only returns 1 value(the humidity percent), whereas the accelerometer returns three (x,y,z values)
        int count = 0;
        Sensor s;

        //sensorIndex is used as an adapter for sensorManager and sensorsAvaible. SensorManager's sensors start at 1, our array starts at index 0
        int sensorIndex;

        //Loop through the preconstructed list of sensors we wish to listen for
        for (int i = 0; i < sensorsAvailable.length; i++) {
            //Once again sensorIndex is used to map sensorsAvaiable to SensorManager's sensors which start at 1
            sensorIndex = i + 1;
            //If we do not care for the sensor then we skip it
            if (!sensorsAvailable[i])
                continue;

            //If we do care look for the default sensor on the device
            s = manager.getDefaultSensor(sensorIndex);
            //Check if it returns null, if it did the device does not have the sensors so set it to false in our list
            //For example the nexus 5 we are using does not have a humidity sensors even though we would like to look for it (set as true in sensorsAvailable)
            if (s == null) {
                sensorsAvailable[i] = false;
                continue;
            }

            //The count is increased by 3 for the sensors that return multiple values i.e.: accelerometer, rotation vector, magnetic field, etc.
            if (sensorIndex == 1 || sensorIndex == 2 || sensorIndex == 3 || sensorIndex == 4 ||
                    sensorIndex == 9 || sensorIndex == 10 || sensorIndex == 11 || sensorIndex == 14
                    || sensorIndex == 15 || sensorIndex == 16 || sensorIndex == 20) {
                count += 3;
            } else {
                count++;
            }
        }

        return count;
    }
}
