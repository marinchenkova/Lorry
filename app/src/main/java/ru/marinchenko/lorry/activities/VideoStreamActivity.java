package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import ru.marinchenko.lorry.R;

public class VideoStreamActivity extends Activity implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {

    private final static String USERNAME = "admin";
    private final static String PASSWORD = "camera";
    private String rtspUrl;

    private ProgressDialog pDialog;

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_stream);

        initVideo();
    }

    public void initVideo(){
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        rtspUrl = Formatter.formatIpAddress(getIntent().getIntExtra("IP", 0));
        rtspUrl = "rtsp://".concat(rtspUrl).concat(":554");

        //TODO find port

        Toast.makeText(getApplicationContext(), rtspUrl, Toast.LENGTH_LONG).show();

        //rtspUrl = "http://www.ustream.tv/exploreOsprey";

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }


    public void exit(View view) {
        onBackPressed();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);

        Context context = getApplicationContext();
        Map<String, String> headers = getRtspHeaders();
        Uri source = Uri.parse(rtspUrl);

        try {
            mediaPlayer.setDataSource(context, source, headers);

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }
        catch (Exception e) {}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.release();
    }

    private Map<String, String> getRtspHeaders() {
        Map<String, String> headers = new HashMap<>();
        String basicAuthValue = getBasicAuthValue(USERNAME, PASSWORD);
        headers.put("Authorization", basicAuthValue);
        return headers;
    }

    private String getBasicAuthValue(String usr, String pwd) {
        String credentials = usr + ":" + pwd;
        int flags = Base64.URL_SAFE | Base64.NO_WRAP;
        byte[] bytes = credentials.getBytes();
        return "Basic " + Base64.encodeToString(bytes, flags);
    }
}
