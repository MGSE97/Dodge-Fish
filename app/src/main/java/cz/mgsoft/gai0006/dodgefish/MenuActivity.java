package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//font - https://stackoverflow.com/questions/2888508/how-to-change-the-font-on-the-textview

public class MenuActivity extends Activity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click);
    }

    public void launchGame(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, ControllerActivity.class);
        startActivity(intent);
    }

    public void showLeaderboard(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void exitApp(View v)
    {
        mediaPlayer.start();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
