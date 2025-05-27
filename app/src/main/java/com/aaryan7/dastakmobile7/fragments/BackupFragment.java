package com.aaryan7.dastakmobile7.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.utils.BackupManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment for backup and restore functionality
 */
public class BackupFragment extends Fragment {
    private static final int RC_SIGN_IN = 1001;
    
    private BackupManager backupManager;
    private TextView tvBackupStatus;
    private MaterialButton btnSignIn, btnBackupNow, btnRestore;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        
        // Initialize views
        tvBackupStatus = view.findViewById(R.id.tv_backup_status);
        btnSignIn = view.findViewById(R.id.btn_sign_in);
        btnBackupNow = view.findViewById(R.id.btn_backup_now);
        btnRestore = view.findViewById(R.id.btn_restore);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize backup manager
        backupManager = new BackupManager(requireContext());
        
        // Set up sign in button
        btnSignIn.setOnClickListener(v -> signIn());
        
        // Set up backup button
        btnBackupNow.setOnClickListener(v -> backupData());
        
        // Set up restore button
        btnRestore.setOnClickListener(v -> restoreData());
        
        // Update UI based on sign-in state
        updateUI();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    
    /**
     * Update UI based on sign-in state
     */
    private void updateUI() {
        if (backupManager.isUserSignedIn()) {
            // User is signed in
            btnSignIn.setText("Sign out");
            btnSignIn.setOnClickListener(v -> signOut());
            btnBackupNow.setEnabled(true);
            btnRestore.setEnabled(true);
            
            // Show last backup time
            long lastBackupTime = backupManager.getLastBackupTime();
            if (lastBackupTime > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(lastBackupTime));
                tvBackupStatus.setText("Last backup: " + formattedDate);
            } else {
                tvBackupStatus.setText("No backup found. Tap 'Backup Now' to create your first backup.");
            }
            
            // Initialize Drive client
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
            if (account != null) {
                backupManager.initializeDriveClient(account);
            }
            
            // Check if automatic backup is needed
            if (backupManager.isBackupNeeded()) {
                backupData();
            }
        } else {
            // User is not signed in
            btnSignIn.setText("Sign in to Google Drive");
            btnSignIn.setOnClickListener(v -> signIn());
            btnBackupNow.setEnabled(false);
            btnRestore.setEnabled(false);
            tvBackupStatus.setText("Not signed in to Google Drive");
        }
    }
    
    /**
     * Sign in to Google
     */
    private void signIn() {
        Intent signInIntent = backupManager.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    /**
     * Sign out from Google
     */
    private void signOut() {
        backupManager.getGoogleSignInClient().signOut().addOnCompleteListener(task -> {
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            updateUI();
        });
    }
    
    /**
     * Backup data to Google Drive
     */
    private void backupData() {
        if (!backupManager.isUserSignedIn()) {
            Toast.makeText(requireContext(), "Please sign in to Google Drive first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        tvBackupStatus.setText("Backing up data...");
        btnBackupNow.setEnabled(false);
        
        backupManager.backupDatabase()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Backup successful", Toast.LENGTH_SHORT).show();
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvBackupStatus.setText("Backup failed: " + e.getMessage());
                    btnBackupNow.setEnabled(true);
                });
    }
    
    /**
     * Restore data from Google Drive
     */
    private void restoreData() {
        if (!backupManager.isUserSignedIn()) {
            Toast.makeText(requireContext(), "Please sign in to Google Drive first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        tvBackupStatus.setText("Restoring data...");
        btnRestore.setEnabled(false);
        
        backupManager.restoreDatabase()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Restore successful. Please restart the app.", Toast.LENGTH_LONG).show();
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Restore failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvBackupStatus.setText("Restore failed: " + e.getMessage());
                    btnRestore.setEnabled(true);
                });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                backupManager.initializeDriveClient(account);
                updateUI();
            } catch (ApiException e) {
                Toast.makeText(requireContext(), "Sign in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                updateUI();
            }
        }
    }
}
