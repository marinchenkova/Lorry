package name.marinchenko.lorryvision.view.activities.web;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class FeedbackActivity extends ToolbarAppCompatActivity {

    public final static int MAX_LINES = 12;
    public final static int MAX_CHARS = 35 * MAX_LINES;

    private EditText editTextEmail;
    private EditText editTextSubject;
    private EditText editTextMsg;
    private RadioGroup radioGroupType;

    private boolean msgOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ActivityInitializer.Feedback.init(this);
        this.editTextEmail = findViewById(R.id.activity_feedback_editText_email);
        this.editTextSubject = findViewById(R.id.activity_feedback_editText_subject);
        this.editTextMsg = findViewById(R.id.activity_feedback_editText_msg);
        this.radioGroupType = findViewById(R.id.activity_feedback_radioGroup_type);

        //TODO Send button active when all fields OK

        this.editTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                msgOk = editable.length() > 0;
            }
        });
    }



    /**
     * Creating menu for toolbar
     * @param menu main menu
     * @return if created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_feedback, menu);
        return true;
    }

    /**
     * Processing toolbar item selection
     * @param item selected item
     * @return if done
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_feedback_toolbar_send:
                Toast.makeText(this, "SENT", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
