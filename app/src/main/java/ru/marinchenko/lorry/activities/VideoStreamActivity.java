package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.services.WifiAgent;

public class VideoStreamActivity extends Activity {

    private String rtspUrl;
    private String user;
    private String password;

    private MediaPlayer mediaPlayer;
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        initVideo();
    }

    public void initVideo(){
        videoView = (VideoView) findViewById(R.id.videoView);

        //user = "admin";
        //password = ""; // gChaZXUQLFo

        int ip = 0;
        while (ip == 0) {
            ip = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress();
        }
        String ipAddress = Formatter.formatIpAddress(ip);

        rtspUrl = "http://" + ipAddress + "/cgi-bin/Config.cgi?action=play&property=";

        Toast.makeText(getApplicationContext(), rtspUrl, Toast.LENGTH_LONG).show();

        //rtspUrl = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

        videoView.setVideoURI(Uri.parse(rtspUrl));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        videoView.start();
    }


    public void exit(View view) {
        Intent disconnection = new Intent(this, WifiAgent.class);
        disconnection.setAction(WifiAgent.DISCONNECT);
        startService(disconnection);

        onBackPressed();
    }

    public void testDisconnect(View view) {
        Intent disconnection = new Intent(this, WifiAgent.class);
        disconnection.setAction(WifiAgent.DISCONNECT);
        startService(disconnection);
    }
}
