package com.aaryan7.dastakmobile7.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aaryan7.dastakmobile7.models.Product;
import com.aaryan7.dastakmobile7.repository.ProductRepository;

import java.util.List;

/**
 * ViewModel for Product operations
 */
public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private MutableLiveData<List<Product>> allProducts;
    private MutableLiveData<Boolean> operationStatus;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        allProducts = new MutableLiveData<>();
        operationStatus = new MutableLiveData<>();
        loadProducts();
    }

    /**
     * Load all products from repository
     */
    private void loadProducts() {
        List<Product> products = repository.getAllProducts();
        allProducts.setValue(products);
    }

    /**
     * Get all products as LiveData
     * @return LiveData of product list
     */
    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    /**
     * Get operation status as LiveData
     * @return LiveData of operation status
     */
    public LiveData<Boolean> getOperationStatus() {
        return operationStatus;
    }

    /**
     * Add a new product
     * @param name Product name
     * @param purchasePrice Purchase price
     * @param sellingPrice Selling price
     * @param quantity Initial quantity
     */
    public void addProduct(String name, double purchasePrice, double sellingPrice, int quantity) {
        long id = repository.addProduct(name, purchasePrice, sellingPrice, quantity);
        operationStatus.setValue(id > 0);
        loadProducts(); // Refresh product list
    }

    /**
     * Update an existing product
     * @param product Product to update
     */
    public void updateProduct(Product product) {
        int result = repository.updateProduct(product);
        operationStatus.setValue(result > 0);
        loadProducts(); // Refresh product list
    }

    /**
     * Delete a product
     * @param product Product to delete
     */
    public void deleteProduct(Product product) {
        repository.deleteProduct(product);
        loadProducts(); // Refresh product list
    }

    /**
     * Update product quantity after sale
     * @param productId Product ID
     * @param soldQuantity Quantity sold
     * @return true if successful, false if not enough stock
     */
    public boolean updateProductQuantity(long productId, int soldQuantity) {
        boolean result = repository.updateProductQuantity(productId, soldQuantity);
        loadProducts(); // Refresh product list
        return result;
    }
}
