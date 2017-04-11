package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.content.Context;
import android.widget.MediaController;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import ru.marinchenko.lorry.R;

public class VideoStreamActivity extends Activity {

    private MainActivity mainActivity;
    private WifiManager wifiManager;
    private MediaController mediaController;
    private String videoSource;
    private VideoView videoView;

    String SrcPath = "rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        mainActivity = (MainActivity) getParent();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(mediaController);
        //videoView.requestFocus();
        //videoView.setOnErrorListener(errorListener);

        initStream();
        play();
    }

    public void initStream(){
        //videoSource = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        //videoSource = "http://".concat(videoSource.concat(":8080/"));

        videoSource = "http://www.ustream.tv/channel/iss-hdev-payload";
        Uri videoUri = Uri.parse(videoSource);

        videoView.setVideoURI(Uri.parse(SrcPath));

        //Toast.makeText(this, videoSource, Toast.LENGTH_LONG).show();
    }


    public void play(){
        videoView.start();
    }

/*
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
*/
    public void exit(View view) {
        onBackPressed();
    }
}
