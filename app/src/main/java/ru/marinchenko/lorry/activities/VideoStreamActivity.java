package ru.marinchenko.lorry.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.text.format.Formatter;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ru.marinchenko.lorry.R;

public class VideoStreamActivity extends Activity implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {

    private String rtspUrl;

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
        rtspUrl = "rtsp://".concat(rtspUrl.concat(":8080/"));

        Toast.makeText(getApplicationContext(), rtspUrl, Toast.LENGTH_LONG).show();

        //rtspUrl = "http://www.ustream.tv/exploreOsprey";

        //surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.addCallback(this);
    }


    public void exit(View view) {
        onBackPressed();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
 /*       mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);

        Context context = getApplicationContext();
        Map<String, String> headers = getRtspHeaders();
        Uri source = Uri.parse(RTSP_URL);

        try {
            // Specify the IP camera's URL and auth headers.
            _mediaPlayer.setDataSource(context, source, headers);

            // Begin the process of setting up a video stream.
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.prepareAsync();
        }
        catch (Exception e) {}*/
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
