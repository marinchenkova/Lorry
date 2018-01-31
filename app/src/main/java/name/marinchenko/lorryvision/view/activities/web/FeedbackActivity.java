package name.marinchenko.lorryvision.view.activities.web;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class FeedbackActivity extends ToolbarAppCompatActivity {

    private EditText editTextMsg;
    private EditText editTextEmail;
    private RadioGroup radioGroupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ActivityInitializer.Feedback.init(this);
        this.editTextMsg = findViewById(R.id.activity_feedback_editText_msg);
        this.editTextEmail = findViewById(R.id.activity_feedback_editText_email);
        this.radioGroupType = findViewById(R.id.activity_feedback_radioGroup_type);
    }


    public void onButtonSendClick(View view) {
        final String email = this.editTextEmail.getText().toString();
        final String msg = editTextMsg.getText().toString();
        final RadioButton checked = findViewById(radioGroupType.getCheckedRadioButtonId());
        final String type = checked.getText().toString();
    }
}
