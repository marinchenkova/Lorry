package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.marinchenko.lorry.R;
import ru.marinchenko.lorry.services.WifiAgent;

public class VideoStreamActivity extends Activity {

    private String url;
    private String user;
    private String password;

    private MediaPlayer mediaPlayer;
    private VideoView videoView;

    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        //initVideo();
        initWeb();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initVideo(){
        /*
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

        // Raspberry Pi
        String urlPi = "http://" + "192.168.1.139" + ":8081";

        url = urlPi;
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        videoView.setVideoURI(Uri.parse(url));
        videoView.start();
        /*
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
        */
    }

    private void initWeb(){
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // Ожидание IP
        int ipInt = 0;
        int count = 0;
        while (ipInt == 0 || count < 50) {
            ipInt = wifiManager.getDhcpInfo().serverAddress;
            count++;
        }
        String ipString = Formatter.formatIpAddress(ipInt);

        Toast.makeText(getApplicationContext(), count + " : " + ipString, Toast.LENGTH_LONG).show();

        // Raspberry Pi
        String urlPi = "http://" + ipString + ":8081";

        // Wifi DVR :: MjpegPlayerFragment.onCreate
        String urlLive = "http://" + ipString + "/cgi-bin/liveMJPEG";

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl(urlPi);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mWebView.reload();
    }



    public void exit(View view) {
        Intent disconnection = new Intent(this, WifiAgent.class);
        disconnection.setAction(WifiAgent.DISCONNECT);
        startService(disconnection);

        onBackPressed();
    }
}
