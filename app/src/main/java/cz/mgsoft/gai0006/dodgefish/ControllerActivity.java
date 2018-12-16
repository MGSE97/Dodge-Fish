package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class ControllerActivity extends Activity {

    MediaPlayer mediaPlayer;

    String Id;
    String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        findViewById(R.id.imageButtonRotation).setEnabled(gyroscopeSensor != null);

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click);

        Bundle data = getIntent().getExtras();
        Id = data.getString("id");
        Name = data.getString("name");
    }

    public void launchGameClick(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("controller", "click");
        intent.putExtra("id", Id);
        intent.putExtra("name", Name);
        startActivity(intent);
    }

    public void launchGameRotation(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("controller", "rotation");
        intent.putExtra("id", Id);
        intent.putExtra("name", Name);
        startActivity(intent);
    }
}
