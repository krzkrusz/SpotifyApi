package pl.edu.pg.spotify.activities;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

import pl.edu.pg.spotify.R;
import pl.edu.pg.spotify.util.SpotifyUtils;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        textView.setText(getCurrentCPUusage());

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //SpotifyUtils.connect(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyUtils.disconnect();
    }

    private void updateUI(){
        mRedrawHandler.sleep(1000);
        textView.setText(getCurrentCPUusage());
    }

    private String getCurrentCPUusage() {
        ProcessBuilder processBuilder;
        String holder = "";
        String[] command = {"sh","-c", "top -n 1"};
        InputStream inputStream;
        Process process;
        byte[] byteArry;
        byteArry = new byte[1024];
        try{
            processBuilder = new ProcessBuilder(command);
            process = processBuilder.start();
            inputStream = process.getInputStream();
            while(inputStream.read(byteArry) != -1){
                holder = holder + new String(byteArry);
            }
            inputStream.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        final String clearedFromNewLines = holder.replace("\n", "");
        final int onlyCPUusageLastIndex = clearedFromNewLines.indexOf("IOW");
        return clearedFromNewLines.substring(0, onlyCPUusageLastIndex - 1);
    }


    private String getCurrentRAMUsage() {
        final long bytesInMegabyte = 1048576L;
        final ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return (mi.totalMem - mi.availMem) / bytesInMegabyte + "MB";
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
