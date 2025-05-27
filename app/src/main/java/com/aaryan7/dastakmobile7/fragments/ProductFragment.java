package com.aaryan7.dastakmobile7.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.adapters.ProductAdapter;
import com.aaryan7.dastakmobile7.models.Product;
import com.aaryan7.dastakmobile7.viewmodel.ProductViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Fragment for Product Management
 */
public class ProductFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private ProductViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView rvProducts;
    private TextInputEditText etProductName, etPurchasePrice, etSellingPrice, etQuantity;
    private MaterialButton btnAddProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        
        // Initialize views
        etProductName = view.findViewById(R.id.et_product_name);
        etPurchasePrice = view.findViewById(R.id.et_purchase_price);
        etSellingPrice = view.findViewById(R.id.et_selling_price);
        etQuantity = view.findViewById(R.id.et_quantity);
        btnAddProduct = view.findViewById(R.id.btn_add_product);
        rvProducts = view.findViewById(R.id.rv_products);
        
        // Setup RecyclerView
        adapter = new ProductAdapter(this);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        
        // Observe products
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });
        
        // Observe operation status
        viewModel.getOperationStatus().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    clearInputFields();
                    Toast.makeText(getContext(), "Operation successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Set click listener for add button
        btnAddProduct.setOnClickListener(v -> addProduct());
    }
    
    /**
     * Add a new product
     */
    private void addProduct() {
        String name = etProductName.getText().toString().trim();
        String purchasePriceStr = etPurchasePrice.getText().toString().trim();
        String sellingPriceStr = etSellingPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        
        // Validate inputs
        if (name.isEmpty() || purchasePriceStr.isEmpty() || sellingPriceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double purchasePrice = Double.parseDouble(purchasePriceStr);
            double sellingPrice = Double.parseDouble(sellingPriceStr);
            int quantity = Integer.parseInt(quantityStr);
            
            // Add product
            viewModel.addProduct(name, purchasePrice, sellingPrice, quantity);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Clear input fields after adding product
     */
    private void clearInputFields() {
        etProductName.setText("");
        etPurchasePrice.setText("");
        etSellingPrice.setText("");
        etQuantity.setText("");
        etProductName.requestFocus();
    }
    
    /**
     * Show dialog to edit product
     * @param product Product to edit
     */
    private void showEditDialog(Product product) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_product, null);
        
        TextInputEditText etEditName = dialogView.findViewById(R.id.et_edit_product_name);
        TextInputEditText etEditPurchasePrice = dialogView.findViewById(R.id.et_edit_purchase_price);
        TextInputEditText etEditSellingPrice = dialogView.findViewById(R.id.et_edit_selling_price);
        TextInputEditText etEditQuantity = dialogView.findViewById(R.id.et_edit_quantity);
        
        // Set current values
        etEditName.setText(product.getName());
        etEditPurchasePrice.setText(String.valueOf(product.getPurchasePrice()));
        etEditSellingPrice.setText(String.valueOf(product.getSellingPrice()));
        etEditQuantity.setText(String.valueOf(product.getQuantity()));
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Product")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get updated values
                    String name = etEditName.getText().toString().trim();
                    String purchasePriceStr = etEditPurchasePrice.getText().toString().trim();
                    String sellingPriceStr = etEditSellingPrice.getText().toString().trim();
                    String quantityStr = etEditQuantity.getText().toString().trim();
                    
                    // Validate inputs
                    if (name.isEmpty() || purchasePriceStr.isEmpty() || sellingPriceStr.isEmpty() || quantityStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    try {
                        double purchasePrice = Double.parseDouble(purchasePriceStr);
                        double sellingPrice = Double.parseDouble(sellingPriceStr);
                        int quantity = Integer.parseInt(quantityStr);
                        
                        // Update product
                        product.setName(name);
                        product.setPurchasePrice(purchasePrice);
                        product.setSellingPrice(sellingPrice);
                        product.setQuantity(quantity);
                        
                        viewModel.updateProduct(product);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Show confirmation dialog to delete product
     * @param product Product to delete
     */
    private void showDeleteConfirmation(Product product) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteProduct(product);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEditClick(Product product) {
        showEditDialog(product);
    }

    @Override
    public void onDeleteClick(Product product) {
        showDeleteConfirmation(product);
    }
}
