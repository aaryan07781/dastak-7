package com.aaryan7.dastakmobile7.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aaryan7.dastakmobile7.R;
import com.google.android.material.button.MaterialButton;

/**
 * Fragment for contact support section
 */
public class SupportFragment extends Fragment {
    private MaterialButton btnSendEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);
        
        // Initialize views
        btnSendEmail = view.findViewById(R.id.btn_send_email);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set up send email button
        btnSendEmail.setOnClickListener(v -> sendSupportEmail());
    }
    
    /**
     * Send support email
     */
    private void sendSupportEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:myuse077@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Dastak Mobile 7 Support");
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show();
        }
    }
}
