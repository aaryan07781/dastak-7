package com.aaryan7.dastakmobile7.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.viewmodel.SalesViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for Sales and Profit Analysis
 */
public class SalesFragment extends Fragment {
    private SalesViewModel viewModel;
    private TabLayout tabTimePeriod;
    private TextView tvTotalSales, tvTotalProfit;
    private LineChart chartSales, chartProfit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        
        // Initialize views
        tabTimePeriod = view.findViewById(R.id.tab_time_period);
        tvTotalSales = view.findViewById(R.id.tv_total_sales);
        tvTotalProfit = view.findViewById(R.id.tv_total_profit);
        chartSales = view.findViewById(R.id.chart_sales);
        chartProfit = view.findViewById(R.id.chart_profit);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SalesViewModel.class);
        
        // Setup tab selection listener
        tabTimePeriod.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setSelectedPeriod(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
        
        // Observe total sales
        viewModel.getTotalSales().observe(getViewLifecycleOwner(), totalSales -> {
            tvTotalSales.setText(String.format(Locale.getDefault(), "₹%.2f", totalSales));
        });
        
        // Observe total profit
        viewModel.getTotalProfit().observe(getViewLifecycleOwner(), totalProfit -> {
            tvTotalProfit.setText(String.format(Locale.getDefault(), "₹%.2f", totalProfit));
        });
        
        // Observe selected period
        viewModel.getSelectedPeriod().observe(getViewLifecycleOwner(), period -> {
            tabTimePeriod.getTabAt(period).select();
        });
        
        // Setup charts
        setupCharts();
    }
    
    /**
     * Setup sales and profit charts
     */
    private void setupCharts() {
        // Get data for charts
        double[] salesData = viewModel.getSalesDataForChart();
        double[] profitData = viewModel.getProfitDataForChart();
        String[] dayLabels = viewModel.getDayLabelsForChart();
        
        // Setup sales chart
        setupSalesChart(salesData, dayLabels);
        
        // Setup profit chart
        setupProfitChart(profitData, dayLabels);
    }
    
    /**
     * Setup sales chart
     * @param salesData Sales data array
     * @param dayLabels Day labels array
     */
    private void setupSalesChart(double[] salesData, String[] dayLabels) {
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < salesData.length; i++) {
            entries.add(new Entry(i, (float) salesData[i]));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "Sales");
        dataSet.setColor(getResources().getColor(R.color.primary));
        dataSet.setValueTextColor(getResources().getColor(R.color.primary_text));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(getResources().getColor(R.color.primary));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        
        LineData lineData = new LineData(dataSet);
        
        chartSales.setData(lineData);
        chartSales.getDescription().setEnabled(false);
        chartSales.getLegend().setEnabled(false);
        
        XAxis xAxis = chartSales.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        
        chartSales.invalidate();
    }
    
    /**
     * Setup profit chart
     * @param profitData Profit data array
     * @param dayLabels Day labels array
     */
    private void setupProfitChart(double[] profitData, String[] dayLabels) {
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < profitData.length; i++) {
            entries.add(new Entry(i, (float) profitData[i]));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "Profit");
        dataSet.setColor(getResources().getColor(R.color.profit_color));
        dataSet.setValueTextColor(getResources().getColor(R.color.primary_text));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(getResources().getColor(R.color.profit_color));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        
        LineData lineData = new LineData(dataSet);
        
        chartProfit.setData(lineData);
        chartProfit.getDescription().setEnabled(false);
        chartProfit.getLegend().setEnabled(false);
        
        XAxis xAxis = chartProfit.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        
        chartProfit.invalidate();
    }
}
