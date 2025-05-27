package com.aaryan7.dastakmobile7.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.models.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying products in a RecyclerView
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.products = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /**
     * Update the adapter with new products
     * @param products New list of products
     */
    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvPurchasePrice;
        private TextView tvSellingPrice;
        private TextView tvProfit;
        private TextView tvQuantity;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPurchasePrice = itemView.findViewById(R.id.tv_purchase_price);
            tvSellingPrice = itemView.findViewById(R.id.tv_selling_price);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }

        public void bind(final Product product) {
            tvProductName.setText(product.getName());
            tvPurchasePrice.setText(String.format(Locale.getDefault(), "%.2f", product.getPurchasePrice()));
            tvSellingPrice.setText(String.format(Locale.getDefault(), "%.2f", product.getSellingPrice()));
            tvProfit.setText(String.format(Locale.getDefault(), "%.2f", product.getProfit()));
            tvQuantity.setText(String.format(Locale.getDefault(), "%d pcs", product.getQuantity()));

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(product);
                }
            });
        }
    }
}
