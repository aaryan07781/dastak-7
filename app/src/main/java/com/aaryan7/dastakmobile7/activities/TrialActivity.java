package com.aaryan7.dastakmobile7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.utils.TrialManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity for trial management and developer activation
 */
public class TrialActivity extends AppCompatActivity {
    private TrialManager trialManager;
    private TextView tvTrialStatus;
    private TextInputEditText etDeveloperId, etDeveloperPassword;
    private MaterialButton btnActivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);
        
        // Initialize trial manager
        trialManager = new TrialManager(this);
        
        // Initialize views
        tvTrialStatus = findViewById(R.id.tv_trial_status);
        etDeveloperId = findViewById(R.id.et_developer_id);
        etDeveloperPassword = findViewById(R.id.et_developer_password);
        btnActivate = findViewById(R.id.btn_activate);
        
        // Update trial status
        updateTrialStatus();
        
        // Set up activation button
        btnActivate.setOnClickListener(v -> activateDeveloperMode());
        
        // Check if trial is active
        if (trialManager.isTrialActive()) {
            // If trial is active, proceed to main activity after a delay
            tvTrialStatus.postDelayed(() -> {
                startActivity(new Intent(TrialActivity.this, MainActivity.class));
                finish();
            }, 2000);
        }
    }
    
    /**
     * Update trial status text
     */
    private void updateTrialStatus() {
        int remainingDays = trialManager.getRemainingTrialDays();
        
        if (trialManager.isDeveloperMode()) {
            tvTrialStatus.setText("Developer Mode Active");
        } else if (remainingDays > 0) {
            tvTrialStatus.setText("Trial Period: " + remainingDays + " days remaining");
        } else {
            tvTrialStatus.setText("Trial Period Expired");
        }
    }
    
    /**
     * Activate developer mode
     */
    private void activateDeveloperMode() {
        String id = etDeveloperId.getText().toString().trim();
        String password = etDeveloperPassword.getText().toString().trim();
        
        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both ID and password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean activated = trialManager.activateDeveloperMode(id, password);
        
        if (activated) {
            Toast.makeText(this, "Developer mode activated successfully", Toast.LENGTH_SHORT).show();
            updateTrialStatus();
            
            // Proceed to main activity
            startActivity(new Intent(TrialActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid developer credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
