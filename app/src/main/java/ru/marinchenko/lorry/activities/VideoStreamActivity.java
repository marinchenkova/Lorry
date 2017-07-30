package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.services.WifiAgent;

public class VideoStreamActivity extends Activity {

    private String url;
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

        // Ожидание IP
        int ip = 0;
        while (ip == 0) {
            ip = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress();
        }
        String ipAddress = Formatter.formatIpAddress(ip);

        // Wifi DVR :: CameraCommand.commandPlayfilestreamingUrl
        //String urlPlay = "http://" + ipAddress + "/cgi-bin/Config.cgi";

        // Wifi DVR :: CameraCommand.commandCameraRecordUrl
        String urlRec =
                "http://" + ipAddress + "/cgi-bin/Config.cgi?action=set&property=Video&value=record";

        // Wifi DVR :: MjpegPlayerFragment.onCreate
        String urlLive = "http://" + ipAddress + "/cgi-bin/liveMJPEG";

        // Wifi DVR :: CameraCommand.commandQueryCameraPreview
        String urlPreview = "http://" + ipAddress + "/cgi-bin/Config.cgi?action=get&property=Camera.Preview";


        url = urlPreview;
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        videoView.setVideoURI(Uri.parse(url));
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
