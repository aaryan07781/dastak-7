package com.aaryan7.dastakmobile7.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aaryan7.dastakmobile7.models.Sales;
import com.aaryan7.dastakmobile7.repository.SalesRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ViewModel for Sales and Profit Analysis
 */
public class SalesViewModel extends AndroidViewModel {
    private SalesRepository repository;
    private MutableLiveData<List<Sales>> salesData;
    private MutableLiveData<Double> totalSales;
    private MutableLiveData<Double> totalProfit;
    private MutableLiveData<Integer> selectedPeriod; // 0: Day, 1: Week, 2: Month, 3: Year

    public SalesViewModel(@NonNull Application application) {
        super(application);
        repository = new SalesRepository(application);
        salesData = new MutableLiveData<>();
        totalSales = new MutableLiveData<>(0.0);
        totalProfit = new MutableLiveData<>(0.0);
        selectedPeriod = new MutableLiveData<>(0); // Default to Day
        loadSalesData();
    }

    /**
     * Load sales data based on selected period
     */
    private void loadSalesData() {
        Date currentDate = new Date();
        List<Sales> data;
        
        switch (selectedPeriod.getValue()) {
            case 1: // Week
                data = repository.getSalesForWeek(currentDate);
                break;
            case 2: // Month
                data = repository.getSalesForMonth(currentDate);
                break;
            case 3: // Year
                data = repository.getSalesForYear(currentDate);
                break;
            case 0: // Day (default)
            default:
                data = repository.getSalesForDay(currentDate);
                break;
        }
        
        salesData.setValue(data);
        calculateTotals(data);
    }

    /**
     * Calculate total sales and profit
     * @param data Sales data list
     */
    private void calculateTotals(List<Sales> data) {
        double salesAmount = repository.getTotalSalesAmount(data);
        double profitAmount = repository.getTotalProfit(data);
        
        totalSales.setValue(salesAmount);
        totalProfit.setValue(profitAmount);
    }

    /**
     * Get sales data as LiveData
     * @return LiveData of sales data list
     */
    public LiveData<List<Sales>> getSalesData() {
        return salesData;
    }

    /**
     * Get total sales as LiveData
     * @return LiveData of total sales amount
     */
    public LiveData<Double> getTotalSales() {
        return totalSales;
    }

    /**
     * Get total profit as LiveData
     * @return LiveData of total profit amount
     */
    public LiveData<Double> getTotalProfit() {
        return totalProfit;
    }

    /**
     * Get selected period as LiveData
     * @return LiveData of selected period
     */
    public LiveData<Integer> getSelectedPeriod() {
        return selectedPeriod;
    }

    /**
     * Set selected period and reload data
     * @param period Period to select (0: Day, 1: Week, 2: Month, 3: Year)
     */
    public void setSelectedPeriod(int period) {
        selectedPeriod.setValue(period);
        loadSalesData();
    }

    /**
     * Add sales data from a bill
     * @param amount Bill amount
     * @param profit Bill profit
     */
    public void addSalesData(double amount, double profit) {
        Sales sales = new Sales(new Date(), amount, profit);
        repository.addSales(sales);
        loadSalesData(); // Reload data to reflect changes
    }

    /**
     * Get sales data for chart (last 7 days)
     * @return Array of sales amounts for last 7 days
     */
    public double[] getSalesDataForChart() {
        double[] data = new double[7];
        Calendar calendar = Calendar.getInstance();
        
        // Get data for last 7 days
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            
            List<Sales> dayData = repository.getSalesForDay(calendar.getTime());
            data[6 - i] = repository.getTotalSalesAmount(dayData);
        }
        
        return data;
    }

    /**
     * Get profit data for chart (last 7 days)
     * @return Array of profit amounts for last 7 days
     */
    public double[] getProfitDataForChart() {
        double[] data = new double[7];
        Calendar calendar = Calendar.getInstance();
        
        // Get data for last 7 days
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            
            List<Sales> dayData = repository.getSalesForDay(calendar.getTime());
            data[6 - i] = repository.getTotalProfit(dayData);
        }
        
        return data;
    }

    /**
     * Get day labels for chart (last 7 days)
     * @return Array of day labels for last 7 days
     */
    public String[] getDayLabelsForChart() {
        String[] labels = new String[7];
        Calendar calendar = Calendar.getInstance();
        
        // Get labels for last 7 days
        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            
            labels[6 - i] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }
        
        return labels;
    }
}
