package name.marinchenko.lorryvision.activities.info;

import android.os.Bundle;

import name.marinchenko.lorryvision.R;
import name.marinchenko.lorryvision.activities.ToolbarAppCompatActivity;
import name.marinchenko.lorryvision.util.Initializer;

import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_INSTRUCTION;
import static name.marinchenko.lorryvision.activities.main.SettingsFragment.PREF_KEY_FOREGROUND_MAIN;

public class InstructionActivity extends ToolbarAppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        this.prefActivityTag = PREF_KEY_FOREGROUND_INSTRUCTION;
        Initializer.Instruction.init(this);
    }
}
