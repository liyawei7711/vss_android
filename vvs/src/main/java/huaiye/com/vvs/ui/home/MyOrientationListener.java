package huaiye.com.vvs.ui.home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];


    private OnOrientationListener onOrientationListener;

    public MyOrientationListener(Context context) {
        this.context = context;
    }

    // 开始
    public void start() {
        // 获得传感器管理器
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        // 初始化地磁场传感器
        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_UI);

        Sensor acceleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acceleSensor, SensorManager.SENSOR_DELAY_UI);

    }

    // 停止检测
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        calculateOrientation();
    }

    float last;
    long currentTime;

    // 计算方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        float  azimuth = (float) Math.toDegrees(values[0]);
//        values[1] = (float) Math.toDegrees(values[1]);
//        values[2] = (float) Math.toDegrees(values[2]);
//        Log.d("MyOrientationListener","values result  = " + values[0] + values[1] + values[2]);

//        if (azimuth < 0) {
//            azimuth += 360;
//        }
//        azimuth = azimuth / 5 * 5;

        if (Math.abs(azimuth - last) < 15) {
            return;
        }

        last = azimuth;

        currentTime = System.currentTimeMillis();
        if (onOrientationListener != null) {
            onOrientationListener.onOrientationChanged(azimuth);
        }
    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.onOrientationListener = onOrientationListener;
    }


    public interface OnOrientationListener {
        void onOrientationChanged(float x);
    }

}
