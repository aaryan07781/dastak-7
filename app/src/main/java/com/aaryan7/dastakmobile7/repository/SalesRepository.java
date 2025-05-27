package com.aaryan7.dastakmobile7.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aaryan7.dastakmobile7.database.DatabaseHelper;
import com.aaryan7.dastakmobile7.models.Sales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Repository class for Sales operations
 */
public class SalesRepository {
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SALE_DATE = "date";
    private static final String COLUMN_SALE_AMOUNT = "amount";
    private static final String COLUMN_SALE_PROFIT = "profit";

    public SalesRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    /**
     * Add sales data
     * @param sales Sales data to add
     * @return ID of the newly added sales data
     */
    public long addSales(Sales sales) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_SALE_DATE, dateFormat.format(sales.getDate()));
        values.put(COLUMN_SALE_AMOUNT, sales.getAmount());
        values.put(COLUMN_SALE_PROFIT, sales.getProfit());
        
        long id = db.insert(TABLE_SALES, null, values);
        db.close();
        
        return id;
    }
    
    /**
     * Get all sales data
     * @return List of all sales data
     */
    public List<Sales> getAllSales() {
        List<Sales> salesList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_SALES + " ORDER BY " + COLUMN_SALE_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                
                try {
                    sales.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SALE_DATE))));
                } catch (ParseException e) {
                    sales.setDate(new Date());
                }
                
                sales.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_AMOUNT)));
                sales.setProfit(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_PROFIT)));
                
                salesList.add(sales);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return salesList;
    }
    
    /**
     * Get sales data for a specific day
     * @param date Date to get sales for
     * @return List of sales data for the specified day
     */
    public List<Sales> getSalesForDay(Date date) {
        List<Sales> salesList = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();
        
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);
        
        String selectQuery = "SELECT * FROM " + TABLE_SALES + 
                            " WHERE " + COLUMN_SALE_DATE + " BETWEEN ? AND ?" +
                            " ORDER BY " + COLUMN_SALE_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { startDateStr, endDateStr });
        
        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                
                try {
                    sales.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SALE_DATE))));
                } catch (ParseException e) {
                    sales.setDate(new Date());
                }
                
                sales.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_AMOUNT)));
                sales.setProfit(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_PROFIT)));
                
                salesList.add(sales);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return salesList;
    }
    
    /**
     * Get sales data for a specific week
     * @param date Date within the week to get sales for
     * @return List of sales data for the specified week
     */
    public List<Sales> getSalesForWeek(Date date) {
        List<Sales> salesList = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();
        
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);
        
        String selectQuery = "SELECT * FROM " + TABLE_SALES + 
                            " WHERE " + COLUMN_SALE_DATE + " BETWEEN ? AND ?" +
                            " ORDER BY " + COLUMN_SALE_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { startDateStr, endDateStr });
        
        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                
                try {
                    sales.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SALE_DATE))));
                } catch (ParseException e) {
                    sales.setDate(new Date());
                }
                
                sales.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_AMOUNT)));
                sales.setProfit(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_PROFIT)));
                
                salesList.add(sales);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return salesList;
    }
    
    /**
     * Get sales data for a specific month
     * @param date Date within the month to get sales for
     * @return List of sales data for the specified month
     */
    public List<Sales> getSalesForMonth(Date date) {
        List<Sales> salesList = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();
        
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);
        
        String selectQuery = "SELECT * FROM " + TABLE_SALES + 
                            " WHERE " + COLUMN_SALE_DATE + " BETWEEN ? AND ?" +
                            " ORDER BY " + COLUMN_SALE_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { startDateStr, endDateStr });
        
        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                
                try {
                    sales.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SALE_DATE))));
                } catch (ParseException e) {
                    sales.setDate(new Date());
                }
                
                sales.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_AMOUNT)));
                sales.setProfit(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_PROFIT)));
                
                salesList.add(sales);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return salesList;
    }
    
    /**
     * Get sales data for a specific year
     * @param date Date within the year to get sales for
     * @return List of sales data for the specified year
     */
    public List<Sales> getSalesForYear(Date date) {
        List<Sales> salesList = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();
        
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();
        
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);
        
        String selectQuery = "SELECT * FROM " + TABLE_SALES + 
                            " WHERE " + COLUMN_SALE_DATE + " BETWEEN ? AND ?" +
                            " ORDER BY " + COLUMN_SALE_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { startDateStr, endDateStr });
        
        if (cursor.moveToFirst()) {
            do {
                Sales sales = new Sales();
                sales.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                
                try {
                    sales.setDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SALE_DATE))));
                } catch (ParseException e) {
                    sales.setDate(new Date());
                }
                
                sales.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_AMOUNT)));
                sales.setProfit(cursor.getDouble(cursor.getColumnIndex(COLUMN_SALE_PROFIT)));
                
                salesList.add(sales);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return salesList;
    }
    
    /**
     * Get total sales amount for a specific period
     * @param salesList List of sales data
     * @return Total sales amount
     */
    public double getTotalSalesAmount(List<Sales> salesList) {
        double total = 0;
        
        for (Sales sales : salesList) {
            total += sales.getAmount();
        }
        
        return total;
    }
    
    /**
     * Get total profit for a specific period
     * @param salesList List of sales data
     * @return Total profit
     */
    public double getTotalProfit(List<Sales> salesList) {
        double total = 0;
        
        for (Sales sales : salesList) {
            total += sales.getProfit();
        }
        
        return total;
    }
}
