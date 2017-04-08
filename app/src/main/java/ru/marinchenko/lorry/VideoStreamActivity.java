package ru.marinchenko.lorry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoStreamActivity extends Activity {

    private MainActivity mainActivity;
    private WifiManager wifiManager;
    private MediaPlayer mediaPlayer;
    private String videoSource;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        mainActivity = (MainActivity) getParent();

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnErrorListener(errorListener);

        //initStream();
        //play();
    }

    public void initStream(){
        //TODO video URL
        videoSource = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        videoSource = "http://".concat(videoSource.concat(":8080/"));

        videoView.setVideoPath(videoSource);

        Toast.makeText(this, videoSource, Toast.LENGTH_LONG).show();
    }


    public void play(){
        videoView.start();
    }


    MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            checkWifiState();
            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
            return false;
        }
    };

    public void checkWifiState(){
        if (mainActivity.isConnectedRight()) {
            initStream();
            play();
        } else {
            ProgressDialog logging = new ProgressDialog(this);
            logging.setMessage(getResources().getString(R.string.logging));
            logging.show();
            logging.setCancelable(false);

            wifiManager.disconnect();
            wifiManager.reconnect();

            if (mainActivity.isConnectedRight()) logging.dismiss();
            initStream();
            play();
        }

    }

    public void exit(View view) {
        onBackPressed();
    }
}
