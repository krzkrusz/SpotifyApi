package pl.edu.pg.spotify.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.edu.pg.spotify.R;
import pl.edu.pg.spotify.util.DeviceUtil;
import pl.edu.pg.spotify.util.SpotifyUtils;
import pl.edu.pg.spotify.util.traffic.ITrafficSpeedListener;
import pl.edu.pg.spotify.util.traffic.TrafficSpeedMeasurer;
import pl.edu.pg.spotify.util.traffic.Utils;

public class MainActivity extends AppCompatActivity {

    private static final boolean SHOW_SPEED_IN_BITS = false;

    TextView trackInfo;
    TextView ramTextView;
    Button nextSongButton;
    TextView cellInfo;
    TextView networkInfo;
    private TextView speedInfo;

    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackInfo = findViewById(R.id.textView);
        ramTextView = findViewById(R.id.textView2);
        cellInfo = findViewById(R.id.textView3);
        networkInfo = findViewById(R.id.textView4);
        speedInfo = findViewById(R.id.textView5);
        nextSongButton = findViewById(R.id.button);
        nextSongButton.setOnClickListener(v -> SpotifyUtils.nextSong());

        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);
        mTrafficSpeedMeasurer.startMeasuring();

        updateUI();
        DeviceUtil.takeScreenshot(MainActivity.this, getWindow());
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyUtils.connect(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyUtils.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);
    }

    private void updateUI(){
        trackInfo.setText(SpotifyUtils.getCurrentTrackInfo());
        ramTextView.setText(DeviceUtil.getCurrentRAMUsage(getSystemService(ACTIVITY_SERVICE)));
        cellInfo.setText(DeviceUtil.getGsmCellInfo((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), this));
        networkInfo.setText(DeviceUtil.getNetworkInfo(getApplicationContext()));
        mRedrawHandler.sleep(1000);
    }

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateUI();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            runOnUiThread(() -> {
                String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                speedInfo.setText("Up Stream Speed: " + upStreamSpeed + "\n" + "Down Stream Speed: " + downStreamSpeed);
            });
        }
    };
}
