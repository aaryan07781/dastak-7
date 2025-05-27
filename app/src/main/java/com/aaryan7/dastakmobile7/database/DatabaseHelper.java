package com.aaryan7.dastakmobile7.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aaryan7.dastakmobile7.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class for product management
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dastakmobile.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_BILLS = "bills";
    private static final String TABLE_BILL_ITEMS = "bill_items";
    private static final String TABLE_SALES = "sales";

    // Common column names
    private static final String COLUMN_ID = "id";
    
    // Products table columns
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PURCHASE_PRICE = "purchase_price";
    private static final String COLUMN_SELLING_PRICE = "selling_price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PROFIT = "profit";
    
    // Bills table columns
    private static final String COLUMN_BILL_DATE = "date";
    private static final String COLUMN_BILL_TOTAL = "total";
    private static final String COLUMN_BILL_DISCOUNT = "discount";
    private static final String COLUMN_BILL_FINAL_AMOUNT = "final_amount";
    
    // Bill items table columns
    private static final String COLUMN_BILL_ID = "bill_id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_ITEM_QUANTITY = "quantity";
    private static final String COLUMN_ITEM_PRICE = "price";
    private static final String COLUMN_ITEM_SUBTOTAL = "subtotal";
    
    // Sales table columns
    private static final String COLUMN_SALE_DATE = "date";
    private static final String COLUMN_SALE_AMOUNT = "amount";
    private static final String COLUMN_SALE_PROFIT = "profit";

    // Create table statements
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCT_NAME + " TEXT,"
            + COLUMN_PURCHASE_PRICE + " REAL,"
            + COLUMN_SELLING_PRICE + " REAL,"
            + COLUMN_QUANTITY + " INTEGER,"
            + COLUMN_PROFIT + " REAL"
            + ")";
            
    private static final String CREATE_TABLE_BILLS = "CREATE TABLE " + TABLE_BILLS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BILL_DATE + " TEXT,"
            + COLUMN_BILL_TOTAL + " REAL,"
            + COLUMN_BILL_DISCOUNT + " REAL,"
            + COLUMN_BILL_FINAL_AMOUNT + " REAL"
            + ")";
            
    private static final String CREATE_TABLE_BILL_ITEMS = "CREATE TABLE " + TABLE_BILL_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BILL_ID + " INTEGER,"
            + COLUMN_PRODUCT_ID + " INTEGER,"
            + COLUMN_ITEM_QUANTITY + " INTEGER,"
            + COLUMN_ITEM_PRICE + " REAL,"
            + COLUMN_ITEM_SUBTOTAL + " REAL,"
            + "FOREIGN KEY(" + COLUMN_BILL_ID + ") REFERENCES " + TABLE_BILLS + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_ID + ")"
            + ")";
            
    private static final String CREATE_TABLE_SALES = "CREATE TABLE " + TABLE_SALES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SALE_DATE + " TEXT,"
            + COLUMN_SALE_AMOUNT + " REAL,"
            + COLUMN_SALE_PROFIT + " REAL"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_BILLS);
        db.execSQL(CREATE_TABLE_BILL_ITEMS);
        db.execSQL(CREATE_TABLE_SALES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Add a new product to the database
     * @param product Product to add
     * @return ID of the newly inserted product
     */
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PURCHASE_PRICE, product.getPurchasePrice());
        values.put(COLUMN_SELLING_PRICE, product.getSellingPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_PROFIT, product.getProfit());
        
        // Insert row
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        
        return id;
    }
    
    /**
     * Get a product by ID
     * @param id Product ID
     * @return Product object
     */
    public Product getProduct(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(
            TABLE_PRODUCTS,
            new String[] { COLUMN_ID, COLUMN_PRODUCT_NAME, COLUMN_PURCHASE_PRICE, 
                          COLUMN_SELLING_PRICE, COLUMN_QUANTITY, COLUMN_PROFIT },
            COLUMN_ID + "=?",
            new String[] { String.valueOf(id) },
            null, null, null, null
        );
        
        if (cursor != null)
            cursor.moveToFirst();
        
        Product product = new Product();
        product.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        product.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
        product.setPurchasePrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PURCHASE_PRICE)));
        product.setSellingPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_SELLING_PRICE)));
        product.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
        
        cursor.close();
        db.close();
        
        return product;
    }
    
    /**
     * Get all products
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                product.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
                product.setPurchasePrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PURCHASE_PRICE)));
                product.setSellingPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_SELLING_PRICE)));
                product.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
                
                productList.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return productList;
    }
    
    /**
     * Update a product
     * @param product Product to update
     * @return Number of rows affected
     */
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getName());
        values.put(COLUMN_PURCHASE_PRICE, product.getPurchasePrice());
        values.put(COLUMN_SELLING_PRICE, product.getSellingPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_PROFIT, product.getProfit());
        
        // Update row
        int result = db.update(
            TABLE_PRODUCTS,
            values,
            COLUMN_ID + " = ?",
            new String[] { String.valueOf(product.getId()) }
        );
        
        db.close();
        return result;
    }
    
    /**
     * Delete a product
     * @param product Product to delete
     */
    public void deleteProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
            TABLE_PRODUCTS,
            COLUMN_ID + " = ?",
            new String[] { String.valueOf(product.getId()) }
        );
        db.close();
    }
    
    /**
     * Update product quantity after sale
     * @param productId Product ID
     * @param soldQuantity Quantity sold
     * @return true if successful, false if not enough stock
     */
    public boolean updateProductQuantity(long productId, int soldQuantity) {
        Product product = getProduct(productId);
        
        if (product.getQuantity() >= soldQuantity) {
            product.decreaseQuantity(soldQuantity);
            updateProduct(product);
            return true;
        }
        
        return false;
    }
}
