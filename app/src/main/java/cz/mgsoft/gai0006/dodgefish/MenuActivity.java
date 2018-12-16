package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//font - https://stackoverflow.com/questions/2888508/how-to-change-the-font-on-the-textview

public class MenuActivity extends Activity {

    MediaPlayer mediaPlayer;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userName = findViewById(R.id.userName);

        // set audio
        mediaPlayer = MediaPlayer.create(this, R.raw.button_click);

        // get user profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        // request account login
        if(account == null)
            signIn();
        else
            userName.setText(account.getDisplayName());
    }

    public void signIn()
    {
        findViewById(R.id.button).setEnabled(false);
        findViewById(R.id.button2).setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public void launchGame(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, ControllerActivity.class);
        intent.putExtra("id", account.getId());
        intent.putExtra("name", account.getDisplayName());
        startActivity(intent);
    }

    public void showLeaderboard(View v)
    {
        mediaPlayer.start();
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        intent.putExtra("id", account.getId());
        intent.putExtra("name", account.getDisplayName());
        startActivity(intent);
    }

    public void exitApp(View v)
    {
        mediaPlayer.start();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void signOut(View v)
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signIn();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            userName.setText(account.getDisplayName());

            findViewById(R.id.button).setEnabled(true);
            findViewById(R.id.button2).setEnabled(true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SingIn", "signInResult:failed code=" + e.getStatusCode());
            exitApp(null);
        }
    }
}
