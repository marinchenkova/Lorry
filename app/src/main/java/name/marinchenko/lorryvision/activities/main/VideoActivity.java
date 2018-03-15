package name.marinchenko.lorryvision.activities.main;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.util.Initializer;
import name.marinchenko.lorryvision.util.net.WifiAgent;

import static name.marinchenko.lorryvision.services.NetScanService.MSG_RETURN_TO_MAIN;


public class VideoActivity extends ToolbarAppCompatActivity {

    public static final int JUMP_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        this.messenger = new Messenger(new MainIncomingHandler(this));
        Initializer.Video.init(this);
    }

    @Override
    public void onBackPressed() {
        WifiAgent.notifyDisconnected(this);
        super.onBackPressed();
    }

    public void onButtonExitClick(View view) {
        onBackPressed();
    }


    protected static class MainIncomingHandler extends ToolbarAppCompatActivity.IncomingHandler {

        public MainIncomingHandler(ToolbarAppCompatActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RETURN_TO_MAIN:

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
