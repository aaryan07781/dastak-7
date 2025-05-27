package com.aaryan7.dastakmobile7.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aaryan7.dastakmobile7.models.Bill;
import com.aaryan7.dastakmobile7.models.BillItem;
import com.aaryan7.dastakmobile7.models.Product;
import com.aaryan7.dastakmobile7.repository.BillRepository;
import com.aaryan7.dastakmobile7.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for Bill operations
 */
public class BillViewModel extends AndroidViewModel {
    private BillRepository billRepository;
    private ProductRepository productRepository;
    private MutableLiveData<List<Product>> availableProducts;
    private MutableLiveData<Bill> currentBill;
    private MutableLiveData<Boolean> operationStatus;

    public BillViewModel(@NonNull Application application) {
        super(application);
        billRepository = new BillRepository(application);
        productRepository = new ProductRepository(application);
        availableProducts = new MutableLiveData<>();
        currentBill = new MutableLiveData<>(new Bill());
        operationStatus = new MutableLiveData<>();
        loadProducts();
    }

    /**
     * Load all available products
     */
    private void loadProducts() {
        List<Product> products = productRepository.getAllProducts();
        availableProducts.setValue(products);
    }

    /**
     * Get all available products as LiveData
     * @return LiveData of product list
     */
    public LiveData<List<Product>> getAvailableProducts() {
        return availableProducts;
    }

    /**
     * Get current bill as LiveData
     * @return LiveData of current bill
     */
    public LiveData<Bill> getCurrentBill() {
        return currentBill;
    }

    /**
     * Get operation status as LiveData
     * @return LiveData of operation status
     */
    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    /**
     * Add a product to the current bill
     * @param productId Product ID
     * @param quantity Quantity to add
     */
    public void addProductToBill(long productId, int quantity) {
        Product product = productRepository.getProductById(productId);
        
        if (product != null && product.getQuantity() >= quantity) {
            Bill bill = currentBill.getValue();
            
            if (bill == null) {
                bill = new Bill();
            }
            
            // Check if product already exists in bill
            boolean productExists = false;
            for (BillItem item : bill.getItems()) {
                if (item.getProductId() == productId) {
                    // Update quantity
                    item.setQuantity(item.getQuantity() + quantity);
                    productExists = true;
                    break;
                }
            }
            
            // Add new item if product doesn't exist in bill
            if (!productExists) {
                BillItem item = new BillItem(productId, product.getName(), quantity, product.getSellingPrice());
                bill.addItem(item);
            }
            
            currentBill.setValue(bill);
            operationStatus.setValue(true);
        } else {
            operationStatus.setValue(false);
        }
    }

    /**
     * Remove an item from the current bill
     * @param position Position of item in the list
     */
    public void removeItemFromBill(int position) {
        Bill bill = currentBill.getValue();
        
        if (bill != null && position >= 0 && position < bill.getItems().size()) {
            List<BillItem> items = new ArrayList<>(bill.getItems());
            items.remove(position);
            bill.setItems(items);
            currentBill.setValue(bill);
        }
    }

    /**
     * Apply discount to the current bill
     * @param discount Discount amount
     */
    public void applyDiscount(double discount) {
        Bill bill = currentBill.getValue();
        
        if (bill != null) {
            bill.setDiscount(discount);
            currentBill.setValue(bill);
        }
    }

    /**
     * Save the current bill
     * @return ID of the saved bill, or -1 if failed
     */
    public long saveBill() {
        Bill bill = currentBill.getValue();
        
        if (bill != null && !bill.getItems().isEmpty()) {
            long billId = billRepository.saveBill(bill);
            
            if (billId > 0) {
                // Reset current bill
                currentBill.setValue(new Bill());
                // Reload products to update quantities
                loadProducts();
                return billId;
            }
        }
        
        return -1;
    }

    /**
     * Get a bill by ID
     * @param billId Bill ID
     * @return Bill object
     */
    public Bill getBill(long billId) {
        return billRepository.getBill(billId);
    }

    /**
     * Get recent bills
     * @param limit Number of bills to retrieve
     * @return List of recent bills
     */
    public List<Bill> getRecentBills(int limit) {
        return billRepository.getRecentBills(limit);
    }
}
