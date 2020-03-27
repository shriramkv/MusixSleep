package android.music.sleep.model;

import android.music.sleep.activities.StartActivity;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public static final long DISCONNECT_TIMEOUT = 10000; // 5 min = 5 * 60 * 1000 ms - Now 10 sec


    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            StartActivity.mediaPlayer.stop();
            StartActivity.mediaPlayer.release();
            StartActivity.mediaPlayer = null;
            finish();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}
