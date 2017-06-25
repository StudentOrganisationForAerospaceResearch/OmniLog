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
    private boolean logging;
    private static final int recordDelay = 10;

    //This is a preconstructed list denoting the sensors we actually care to listen for, false represents those we don't such as heart rate etc.
    //                                  accel, mag,       ,gyro,light, press,     , proxi, grav, LA,  rota, humid, temp,                      sigmo                geo-rot,                                                 station,motion
    static boolean[] sensorsAvailable = {true, true, false, true, true, true, false, false, true, true, true, true, true, false, false, false, false, false, false, true,};
    //                                    1     2      3     4     5     6     7      8      9    10    11    12    13    14     15     16     17     18     19     20
    //
    /*String[][] sensors = new String[][]{
            {}
    };*/
    int[] sensorMap = new int[sensorsAvailable.length];

    public DataGatherer(Context context, Record recorder) {
        this.context = context;
        this.record = recorder;
        sm = (SensorManager)this.context.getSystemService(SENSOR_SERVICE);
        lm = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);

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
            s = sm.getDefaultSensor(sensorIndex);
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

        //Increase it by 3 because we would like to add the gps Latitude, Longitude, and Altitude
        count += 3;
        //Construct a new array of Strings that will be used to form the "header" of the file (its format)
        //It will list the names of the sensors fields in the order that we will be writing them
        sensors = new String[count];
        //Start count back at 0, waste not want not (we'll use it for this loop)
        count = 0;
        //Run through the new list of available sensors (with the ones we don't have crossed out)
        for (int i = 0; i < sensorsAvailable.length; i++) {
            //If the sensor is not available
            if (!sensorsAvailable[i]) continue;

            //If it is we will append the field name's associated with the sensor to our sensor field list
            //Count references how "full" our sensor field array is, and increments by the number of fields added to i

            switch (i) {
                case 0:
                    sensorMap[i] = count;
                    sensors[count] = "Accelerometer_X";
                    sensors[count + 1] = "Accelerometer_Y";
                    sensors[count + 2] = "Accelerometer_Z";
                    count += 3;
                    break;
                case 1:
                    sensorMap[i] = count;
                    sensors[count] = "Magnetic_Field_X";
                    sensors[count + 1] = "Magnetic_Field_Y";
                    sensors[count + 2] = "Magnetic_Field_Z";
                    count += 3;
                    break;
                case 2:
                    sensorMap[i] = count;
                    sensors[count] = "Orientation_Azimuth";
                    sensors[count + 1] = "Orientation_Pitch";
                    sensors[count + 2] = "Orientation_Roll";
                    count += 3;
                    break;
                case 3:
                    sensorMap[i] = count;
                    sensors[count] = "Gyroscope_X";
                    sensors[count + 1] = "Gyroscope_Y";
                    sensors[count + 2] = "Gyroscope_Z";
                    count += 3;
                    break;
                case 4:
                    sensorMap[i] = count;
                    sensors[count] = "Light(lx)";
                    count++;
                    break;
                case 5:
                    sensorMap[i] = count;
                    sensors[count] = "Pressure(hPa)";
                    count++;
                    break;
                case 6:
                    sensorMap[i] = count;
                    sensors[count] = "Temperature(C)";
                    count++;
                    break;
                case 7:
                    sensorMap[i] = count;
                    sensors[count] = "Proximity(cm)";
                    count++;
                    break;
                case 8:
                    sensorMap[i] = count;
                    sensors[count] = "Gravity_X";
                    sensors[count + 1] = "Gravity_Y";
                    sensors[count + 2] = "Gravity_Z";
                    count += 3;
                    break;
                case 9:
                    sensorMap[i] = count;
                    sensors[count] = "LinAcc_X";
                    sensors[count + 1] = "LinAcc_Y";
                    sensors[count + 2] = "LinAcc_Z";
                    count += 3;
                    break;
                case 10:
                    sensorMap[i] = count;
                    sensors[count] = "Rotation_X";
                    sensors[count + 1] = "Rotation_Y";
                    sensors[count + 2] = "Rotation_Z";
                    count += 3;
                    break;
                case 11:
                    sensorMap[i] = count;
                    sensors[count] = "Humidity(%)";
                    count++;
                    break;
                case 12:
                    sensorMap[i] = count;
                    sensors[count] = "AmbTemp(C)";
                    count++;
                    break;
                case 13:
                    sensorMap[i] = count;
                    sensors[count] = "MagFieldUncal_X";
                    sensors[count + 1] = "MagFieldUncal_Y";
                    sensors[count + 2] = "MagFieldUncal_Z";
                    count += 3;
                    break;
                case 14:
                    sensorMap[i] = count;
                    sensors[count] = "GameRotateVector_X";
                    sensors[count + 1] = "GameRotateVector_Y";
                    sensors[count + 2] = "GameRotateVector_Z";
                    count += 3;
                    break;
                case 15:
                    sensorMap[i] = count;
                    sensors[count] = "GyroUncal_X";
                    sensors[count + 1] = "GyroUncal_Y";
                    sensors[count + 2] = "GyroUncal_Z";
                    count += 3;
                    break;
                case 16:
                    sensorMap[i] = count;
                    sensors[count] = "Sigmo";
                    count++;
                    break;
                case 17:
                    sensorMap[i] = count;
                    sensors[count] = "Steps";
                    count++;
                    break;
                case 18:
                    sensorMap[i] = count;
                    sensors[count] = "StepCount";
                    count++;
                    break;
                case 19:
                    sensorMap[i] = count;
                    sensors[count] = "GeomagRotation_X";
                    sensors[count + 1] = "GeomagRotation_Y";
                    sensors[count + 2] = "GeomagRotation_Z";
                    count += 3;
                    break;
            }
        }

        //Attempt to add the gps fields if it is available
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                sensors[count] = "GPS_Latitude";
                sensors[count + 1] = "GPS_Longitude";
                sensors[count + 2] = "GPS_Altitude";
                gpsIndex = count;
            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                sensors[count] = "Network_Latitude";
                sensors[count + 1] = "Network_Longitude";
                sensors[count + 2] = "Network_Altitude";
                gpsIndex = count;
            }
        } catch (SecurityException e) {
            Log.e("Error", "Security exception when checking GPS");
        }

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
        logging = true;
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
        logging = false;
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
        for (int i = 0; i < sensorMap.length; i++) {
            Log.d("SensorMap", Integer.toString(sensorMap[i]));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int i = sensorMap[event.sensor.getType() - 1];
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
}
