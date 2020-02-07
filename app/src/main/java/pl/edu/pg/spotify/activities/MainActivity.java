package pl.edu.pg.spotify.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.edu.pg.spotify.R;
import pl.edu.pg.spotify.util.DeviceUtil;
import pl.edu.pg.spotify.util.SpotifyUtils;

public class MainActivity extends AppCompatActivity {

    TextView cpuTextView;
    TextView ramTextView;

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpuTextView = findViewById(R.id.textView);
        ramTextView = findViewById(R.id.textView2);

        updateUI();
        DeviceUtil.takeScreenshot(MainActivity.this, getWindow());
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpotifyUtils.connect(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyUtils.disconnect();
    }

    private void updateUI(){
        //cpuTextView.setText(DeviceUtil.getCurrentCPUusage());
        ramTextView.setText(DeviceUtil.getCurrentRAMUsage(getSystemService(ACTIVITY_SERVICE)));
        DeviceUtil.getNetworkInfo(getApplicationContext());
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
}
