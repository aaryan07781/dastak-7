package com.aaryan7.dastakmobile7.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aaryan7.dastakmobile7.database.DatabaseHelper;
import com.aaryan7.dastakmobile7.models.Bill;
import com.aaryan7.dastakmobile7.models.BillItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Repository class for Bill operations
 */
public class BillRepository {
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private static final String TABLE_BILLS = "bills";
    private static final String TABLE_BILL_ITEMS = "bill_items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BILL_DATE = "date";
    private static final String COLUMN_BILL_TOTAL = "total";
    private static final String COLUMN_BILL_DISCOUNT = "discount";
    private static final String COLUMN_BILL_FINAL_AMOUNT = "final_amount";
    private static final String COLUMN_BILL_ID = "bill_id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_ITEM_QUANTITY = "quantity";
    private static final String COLUMN_ITEM_PRICE = "price";
    private static final String COLUMN_ITEM_SUBTOTAL = "subtotal";

    public BillRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    /**
     * Save a bill to the database
     * @param bill Bill to save
     * @return ID of the newly saved bill
     */
    public long saveBill(Bill bill) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Begin transaction
        db.beginTransaction();
        
        try {
            // Insert bill
            ContentValues billValues = new ContentValues();
            billValues.put(COLUMN_BILL_DATE, dateFormat.format(bill.getDate()));
            billValues.put(COLUMN_BILL_TOTAL, bill.getTotal());
            billValues.put(COLUMN_BILL_DISCOUNT, bill.getDiscount());
            billValues.put(COLUMN_BILL_FINAL_AMOUNT, bill.getFinalAmount());
            
            long billId = db.insert(TABLE_BILLS, null, billValues);
            
            // Insert bill items
            for (BillItem item : bill.getItems()) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_BILL_ID, billId);
                itemValues.put(COLUMN_PRODUCT_ID, item.getProductId());
                itemValues.put(COLUMN_PRODUCT_NAME, item.getProductName());
                itemValues.put(COLUMN_ITEM_QUANTITY, item.getQuantity());
                itemValues.put(COLUMN_ITEM_PRICE, item.getPrice());
                itemValues.put(COLUMN_ITEM_SUBTOTAL, item.getSubtotal());
                
                db.insert(TABLE_BILL_ITEMS, null, itemValues);
                
                // Update product quantity
                dbHelper.updateProductQuantity(item.getProductId(), item.getQuantity());
            }
            
            // Set transaction successful
            db.setTransactionSuccessful();
            
            return billId;
        } finally {
            // End transaction
            db.endTransaction();
            db.close();
        }
    }
    
    /**
     * Get a bill by ID
     * @param billId Bill ID
     * @return Bill object
     */
    public Bill getBill(long billId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Query bill
        Cursor billCursor = db.query(
            TABLE_BILLS,
            new String[] { COLUMN_ID, COLUMN_BILL_DATE, COLUMN_BILL_TOTAL, 
                          COLUMN_BILL_DISCOUNT, COLUMN_BILL_FINAL_AMOUNT },
            COLUMN_ID + "=?",
            new String[] { String.valueOf(billId) },
            null, null, null, null
        );
        
        if (billCursor == null || !billCursor.moveToFirst()) {
            return null;
        }
        
        // Create bill object
        Bill bill = new Bill();
        bill.setId(billCursor.getLong(billCursor.getColumnIndex(COLUMN_ID)));
        
        try {
            bill.setDate(dateFormat.parse(billCursor.getString(billCursor.getColumnIndex(COLUMN_BILL_DATE))));
        } catch (ParseException e) {
            bill.setDate(new Date());
        }
        
        bill.setTotal(billCursor.getDouble(billCursor.getColumnIndex(COLUMN_BILL_TOTAL)));
        bill.setDiscount(billCursor.getDouble(billCursor.getColumnIndex(COLUMN_BILL_DISCOUNT)));
        bill.setFinalAmount(billCursor.getDouble(billCursor.getColumnIndex(COLUMN_BILL_FINAL_AMOUNT)));
        
        billCursor.close();
        
        // Query bill items
        Cursor itemsCursor = db.query(
            TABLE_BILL_ITEMS,
            new String[] { COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_PRODUCT_NAME, 
                          COLUMN_ITEM_QUANTITY, COLUMN_ITEM_PRICE, COLUMN_ITEM_SUBTOTAL },
            COLUMN_BILL_ID + "=?",
            new String[] { String.valueOf(billId) },
            null, null, null, null
        );
        
        List<BillItem> items = new ArrayList<>();
        
        if (itemsCursor.moveToFirst()) {
            do {
                BillItem item = new BillItem();
                item.setId(itemsCursor.getLong(itemsCursor.getColumnIndex(COLUMN_ID)));
                item.setBillId(billId);
                item.setProductId(itemsCursor.getLong(itemsCursor.getColumnIndex(COLUMN_PRODUCT_ID)));
                item.setProductName(itemsCursor.getString(itemsCursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
                item.setQuantity(itemsCursor.getInt(itemsCursor.getColumnIndex(COLUMN_ITEM_QUANTITY)));
                item.setPrice(itemsCursor.getDouble(itemsCursor.getColumnIndex(COLUMN_ITEM_PRICE)));
                item.setSubtotal(itemsCursor.getDouble(itemsCursor.getColumnIndex(COLUMN_ITEM_SUBTOTAL)));
                
                items.add(item);
            } while (itemsCursor.moveToNext());
        }
        
        itemsCursor.close();
        db.close();
        
        bill.setItems(items);
        
        return bill;
    }
    
    /**
     * Get all bills
     * @return List of all bills
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_BILLS + " ORDER BY " + COLUMN_BILL_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                long billId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                Bill bill = getBill(billId);
                if (bill != null) {
                    bills.add(bill);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return bills;
    }
    
    /**
     * Get recent bills
     * @param limit Number of bills to retrieve
     * @return List of recent bills
     */
    public List<Bill> getRecentBills(int limit) {
        List<Bill> bills = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_BILLS + 
                            " ORDER BY " + COLUMN_BILL_DATE + " DESC LIMIT " + limit;
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                long billId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                Bill bill = getBill(billId);
                if (bill != null) {
                    bills.add(bill);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return bills;
    }
}
