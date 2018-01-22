package name.marinchenko.lorryvision.view.activities;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;

public class InstructionActivity extends ToolbarActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        initToolbar(
                R.id.toolbar_instruction,
                R.string.activity_instruction,
                true
        );
    }
}
