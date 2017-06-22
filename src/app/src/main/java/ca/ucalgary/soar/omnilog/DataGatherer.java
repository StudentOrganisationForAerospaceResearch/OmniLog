package ca.ucalgary.soar.omnilog;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Colt on 18/06/2017.
 */

public class DataGatherer implements SensorEventListener, LocationListener {
    private SensorManager sm;
    private LocationManager lm;
    private Record record;
    private int gpsIndex;
    private String[] sensors;
    private float[] data;
    private boolean logging;
    private static final int recordDelay = 10;
    //private SensorEventListener

    //                           accel, mag,       ,gyro,light, press,     , proxi, grav, LA,  rota, humid, temp,                      sigmo                    geo-rot,                                                 station,motion
    //boolean[] sensorsAvailable = {true, true, false, true, true, true, false, false, true, true, true, true, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false};
    //                              1     2      3     4     5     6     7      8      9    10    11    12    13    14     15     16     17     18     19     20     21     22     23     24     25     26     27     28     29     30
    //                           accel, mag,       ,gyro,light, press,     , proxi, grav, LA,  rota, humid, temp,                      sigmo                geo-rot,                                                 station,motion
    boolean[] sensorsAvailable = {true, true, false, true, true, true, false, false, true, true, true, true, true, false, false, false, false, false, false, true,};
    //                              1     2      3     4     5     6     7      8      9    10    11    12    13    14     15     16     17     18     19     20
    //
    /*String[][] sensors = new String[][]{
            {}
    };*/
    int[] sensorMap = new int[sensorsAvailable.length];

    public DataGatherer(SensorManager SM, LocationManager LM) {
        sm = SM;
        lm = LM;

        int count = 0;
        Sensor s;
        int j;
        for (int i = 0; i < sensorsAvailable.length; i++) {
            j = i + 1;
            if (!sensorsAvailable[i])
                continue;

            s = sm.getDefaultSensor(j);
            if (s == null) {
                sensorsAvailable[i] = false;
                continue;
            }
            if (j == 1 || j == 2 || j == 3 || j == 4 || j == 9 || j == 10 || j == 11 || j == 14 || j == 15 || j == 16 || j == 20) {
                count += 3;
            } else {
                count++;
            }
        }
        count += 3;
        sensors = new String[count];
        count = 0;
        for (int i = 0; i < sensorsAvailable.length; i++) {
            if (!sensorsAvailable[i])
                continue;
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
        }
        data = new float[sensors.length];
        testSensorsAvailable();
        testSensorString(sensors);
        testMapTo();
    }

    public void startLogging() {
        Sensor s;
        for (int i = 1; i <= sensorsAvailable.length; i++) {
            s = sm.getDefaultSensor(i);
            sm.registerListener(this, s, sm.SENSOR_DELAY_FASTEST);
        }
        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        } catch (SecurityException e) {
        }
        logging = true;
        record = new Record("Record");
        record.writeToFile(sensors);
        Runnable r = new Runnable() {
            public void run() {
                while (logging) {
                    try {
                        Thread.sleep(recordDelay);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    record.writeToFile(data);

                }
            }
        };
        new Thread(r).start();
    }

    public void stopLogging() {
        sm.unregisterListener(this);
        lm.removeUpdates(this);
        logging = false;
        record.closeFile();
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
