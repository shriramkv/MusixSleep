package android.music.sleep.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.music.sleep.R;
import android.music.sleep.activities.StartActivity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.DetectedActivity;

public class BaseActivity extends AppCompatActivity {

    public static final long DISCONNECT_TIMEOUT = 10000; // 15 min = 15 * 60 * 1000 ms = 900000
    BroadcastReceiver broadcastReceiver;

    private Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            stopAndExit();
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public void stopAndExit() {
        startTracking();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("activity_intent")) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };
    }

    private void handleUserActivity(int type, int confidence) {

        String label = "";

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "IN VEHICLE";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "ON BICYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "ON FOOT";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "RUNNING";
                break;
            }
            case DetectedActivity.STILL: {
                label = "STILL";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "TILTING";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "WALKING";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "UNKNOWN";
                break;
            }
        }

        Log.e("ACTIVITY-RECOGNITION", "User activity: " + label + ", Confidence: " + confidence);

        if ((type == DetectedActivity.STILL) && (confidence > 90)) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartActivity.mediaPlayer.setVolume(0.5f, 0.5f);
                    Log.d("Volume", "Reduced by 50%");
                }
            }, 10000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartActivity.mediaPlayer.stop();
                    StartActivity.mediaPlayer.release();
                    StartActivity.mediaPlayer = null;
                    StartActivity.playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    finish();
                }
            }, 10000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("activity_intent"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {
        Intent intent1 = new Intent(BaseActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }
}
