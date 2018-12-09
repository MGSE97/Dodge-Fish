package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.DodgeFish);

         Bundle extras = getIntent().getExtras();
         if(extras.containsKey("controller"))
         {
             String contoller = extras.getString("controller");
             if(contoller.equals("rotation"))
             {
                 SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                 gameView.GyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                 gameView.UseGyroscope = true;

                 gameView.CreateGyroscopeListener(sensorManager);
             }
             else
                 gameView.UseGyroscope = false;
         }

        /*gameView.Start();*/
    }

    @Override
    public void onBackPressed()
    {
        gameView.Stop();

        super.onBackPressed();
    }
}
