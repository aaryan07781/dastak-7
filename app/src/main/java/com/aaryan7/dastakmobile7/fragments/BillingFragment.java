package com.aaryan7.dastakmobile7.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.adapters.BillItemAdapter;
import com.aaryan7.dastakmobile7.models.Bill;
import com.aaryan7.dastakmobile7.models.Product;
import com.aaryan7.dastakmobile7.utils.PDFGenerator;
import com.aaryan7.dastakmobile7.viewmodel.BillViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for Billing
 */
public class BillingFragment extends Fragment implements BillItemAdapter.OnItemRemoveListener {
    private BillViewModel viewModel;
    private BillItemAdapter adapter;
    private RecyclerView rvBillItems;
    private AutoCompleteTextView dropdownProducts;
    private TextInputEditText etQuantity, etDiscount;
    private TextView tvSubtotal, tvFinalAmount;
    private MaterialButton btnAddToBill, btnApplyDiscount, btnSaveBill, btnGeneratePdf;
    
    private Map<String, Long> productMap;
    private PDFGenerator pdfGenerator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing, container, false);
        
        // Initialize views
        dropdownProducts = view.findViewById(R.id.dropdown_products);
        etQuantity = view.findViewById(R.id.et_quantity);
        etDiscount = view.findViewById(R.id.et_discount);
        tvSubtotal = view.findViewById(R.id.tv_subtotal);
        tvFinalAmount = view.findViewById(R.id.tv_final_amount);
        btnAddToBill = view.findViewById(R.id.btn_add_to_bill);
        btnApplyDiscount = view.findViewById(R.id.btn_apply_discount);
        btnSaveBill = view.findViewById(R.id.btn_save_bill);
        btnGeneratePdf = view.findViewById(R.id.btn_generate_pdf);
        rvBillItems = view.findViewById(R.id.rv_bill_items);
        
        // Setup RecyclerView
        adapter = new BillItemAdapter(this);
        rvBillItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBillItems.setAdapter(adapter);
        
        // Initialize product map
        productMap = new HashMap<>();
        
        // Initialize PDF generator
        pdfGenerator = new PDFGenerator(getContext());
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BillViewModel.class);
        
        // Observe available products
        viewModel.getAvailableProducts().observe(getViewLifecycleOwner(), products -> {
            setupProductDropdown(products);
        });
        
        // Observe current bill
        viewModel.getCurrentBill().observe(getViewLifecycleOwner(), bill -> {
            adapter.setItems(bill.getItems());
            updateBillSummary(bill);
        });
        
        // Set click listeners
        btnAddToBill.setOnClickListener(v -> addProductToBill());
        btnApplyDiscount.setOnClickListener(v -> applyDiscount());
        btnSaveBill.setOnClickListener(v -> saveBill());
        btnGeneratePdf.setOnClickListener(v -> generatePdf());
    }
    
    /**
     * Setup product dropdown
     * @param products List of available products
     */
    private void setupProductDropdown(List<Product> products) {
        List<String> productNames = new ArrayList<>();
        productMap.clear();
        
        for (Product product : products) {
            if (product.getQuantity() > 0) {
                String displayName = product.getName() + " (₹" + product.getSellingPrice() + ")";
                productNames.add(displayName);
                productMap.put(displayName, product.getId());
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                productNames);
        
        dropdownProducts.setAdapter(adapter);
    }
    
    /**
     * Add selected product to bill
     */
    private void addProductToBill() {
        String selectedProduct = dropdownProducts.getText().toString();
        String quantityStr = etQuantity.getText().toString().trim();
        
        if (selectedProduct.isEmpty()) {
            Toast.makeText(getContext(), "Please select a product", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!productMap.containsKey(selectedProduct)) {
            Toast.makeText(getContext(), "Invalid product selection", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(getContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            
            long productId = productMap.get(selectedProduct);
            viewModel.addProductToBill(productId, quantity);
            
            // Clear selection
            dropdownProducts.setText("");
            etQuantity.setText("1");
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Apply discount to bill
     */
    private void applyDiscount() {
        String discountStr = etDiscount.getText().toString().trim();
        
        if (discountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter discount amount", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double discount = Double.parseDouble(discountStr);
            if (discount < 0) {
                Toast.makeText(getContext(), "Discount cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
            
            viewModel.applyDiscount(discount);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid discount amount", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Save bill
     */
    private void saveBill() {
        long billId = viewModel.saveBill();
        
        if (billId > 0) {
            Toast.makeText(getContext(), "Bill saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to save bill", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Generate PDF bill
     */
    private void generatePdf() {
        Bill bill = viewModel.getCurrentBill().getValue();
        
        if (bill == null || bill.getItems().isEmpty()) {
            Toast.makeText(getContext(), "No items in bill", Toast.LENGTH_SHORT).show();
            return;
        }
        
        File pdfFile = pdfGenerator.generateBillPdf(bill);
        
        if (pdfFile != null) {
            Toast.makeText(getContext(), "PDF generated: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Update bill summary
     * @param bill Current bill
     */
    private void updateBillSummary(Bill bill) {
        tvSubtotal.setText(String.format(Locale.getDefault(), "₹%.2f", bill.getTotal()));
        tvFinalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", bill.getFinalAmount()));
    }

    @Override
    public void onItemRemove(int position) {
        viewModel.removeItemFromBill(position);
    }
}
