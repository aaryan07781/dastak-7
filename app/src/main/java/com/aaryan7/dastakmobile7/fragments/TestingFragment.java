package com.aaryan7.dastakmobile7.fragments;

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
import com.aaryan7.dastakmobile7.utils.TestUtils;
import com.google.android.material.button.MaterialButton;

import java.io.File;

/**
 * Fragment for testing app components and performance
 */
public class TestingFragment extends Fragment {
    private TextView tvTestResults, tvBuildStatus;
    private MaterialButton btnRunTests, btnStressTest, btnCheckMemory, btnValidateBuild;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_testing, container, false);
        
        // Initialize views
        tvTestResults = view.findViewById(R.id.tv_test_results);
        tvBuildStatus = view.findViewById(R.id.tv_build_status);
        btnRunTests = view.findViewById(R.id.btn_run_tests);
        btnStressTest = view.findViewById(R.id.btn_stress_test);
        btnCheckMemory = view.findViewById(R.id.btn_check_memory);
        btnValidateBuild = view.findViewById(R.id.btn_validate_build);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set up run tests button
        btnRunTests.setOnClickListener(v -> runComponentTests());
        
        // Set up stress test button
        btnStressTest.setOnClickListener(v -> runStressTest());
        
        // Set up check memory button
        btnCheckMemory.setOnClickListener(v -> checkMemoryUsage());
        
        // Set up validate build button
        btnValidateBuild.setOnClickListener(v -> validateBuildFiles());
    }
    
    /**
     * Run component tests
     */
    private void runComponentTests() {
        tvTestResults.setText("Running component tests...");
        
        // Run tests in background thread
        new Thread(() -> {
            final String results = TestUtils.testAllComponents(requireContext());
            
            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                tvTestResults.setText(results);
                Toast.makeText(requireContext(), "Component tests completed", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
    
    /**
     * Run stress test
     */
    private void runStressTest() {
        tvTestResults.setText("Running stress test...");
        
        // Run tests in background thread
        new Thread(() -> {
            final String results = TestUtils.performStressTest(requireContext(), 100);
            
            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                tvTestResults.setText(results);
                Toast.makeText(requireContext(), "Stress test completed", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
    
    /**
     * Check memory usage
     */
    private void checkMemoryUsage() {
        tvTestResults.setText("Checking memory usage...");
        
        // Run in background thread
        new Thread(() -> {
            final String results = TestUtils.checkMemoryUsage();
            
            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                tvTestResults.setText(results);
                Toast.makeText(requireContext(), "Memory check completed", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
    
    /**
     * Validate build files
     */
    private void validateBuildFiles() {
        tvBuildStatus.setText("Validating build files...");
        
        // Run in background thread
        new Thread(() -> {
            StringBuilder results = new StringBuilder();
            
            // Check for required Gradle files
            String[] requiredFiles = {
                    "/build.gradle",
                    "/app/build.gradle",
                    "/settings.gradle",
                    "/gradle/wrapper/gradle-wrapper.properties",
                    "/gradle/wrapper/gradle-wrapper.jar",
                    "/gradlew",
                    "/gradlew.bat",
                    "/codemagic.yaml"
            };
            
            boolean allFilesExist = true;
            
            for (String filePath : requiredFiles) {
                File file = new File(requireContext().getFilesDir().getParentFile().getParentFile(), filePath);
                if (file.exists()) {
                    results.append("✓ ").append(filePath).append(" exists\n");
                } else {
                    results.append("✗ ").append(filePath).append(" is missing\n");
                    allFilesExist = false;
                }
            }
            
            if (allFilesExist) {
                results.append("\nAll build files are present and ready for Codemagic!");
            } else {
                results.append("\nSome required files are missing. Please check the list above.");
            }
            
            final String finalResults = results.toString();
            
            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                tvBuildStatus.setText(finalResults);
                Toast.makeText(requireContext(), "Build validation completed", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
