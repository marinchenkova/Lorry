package name.marinchenko.lorryvision.view.activities.info;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.view.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.view.util.ActivityInitializer;

public class InstructionActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        ActivityInitializer.Instruction.init(this);
    }
}
