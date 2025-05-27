package com.aaryan7.dastakmobile7.repository;

import android.content.Context;

import com.aaryan7.dastakmobile7.database.DatabaseHelper;
import com.aaryan7.dastakmobile7.models.Product;

import java.util.List;

/**
 * Repository class for Product operations
 */
public class ProductRepository {
    private DatabaseHelper dbHelper;

    public ProductRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add a new product
     * @param name Product name
     * @param purchasePrice Purchase price
     * @param sellingPrice Selling price
     * @param quantity Initial quantity
     * @return ID of the newly added product
     */
    public long addProduct(String name, double purchasePrice, double sellingPrice, int quantity) {
        Product product = new Product(name, purchasePrice, sellingPrice, quantity);
        return dbHelper.addProduct(product);
    }

    /**
     * Get all products
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return dbHelper.getAllProducts();
    }

    /**
     * Get product by ID
     * @param id Product ID
     * @return Product object
     */
    public Product getProductById(long id) {
        return dbHelper.getProduct(id);
    }

    /**
     * Update product details
     * @param product Product to update
     * @return Number of rows affected
     */
    public int updateProduct(Product product) {
        return dbHelper.updateProduct(product);
    }

    /**
     * Delete a product
     * @param product Product to delete
     */
    public void deleteProduct(Product product) {
        dbHelper.deleteProduct(product);
    }

    /**
     * Update product quantity after sale
     * @param productId Product ID
     * @param soldQuantity Quantity sold
     * @return true if successful, false if not enough stock
     */
    public boolean updateProductQuantity(long productId, int soldQuantity) {
        return dbHelper.updateProductQuantity(productId, soldQuantity);
    }
}
