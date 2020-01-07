package pl.edu.pg.spotify.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pl.edu.pg.spotify.R;
import pl.edu.pg.spotify.util.SpotifyUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}
