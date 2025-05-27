package com.aaryan7.dastakmobile7.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.models.BillItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying bill items in a RecyclerView
 */
public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.BillItemViewHolder> {
    private List<BillItem> items;
    private OnItemRemoveListener listener;

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public BillItemAdapter(OnItemRemoveListener listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new BillItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillItemViewHolder holder, int position) {
        BillItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Update the adapter with new items
     * @param items New list of items
     */
    public void setItems(List<BillItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    class BillItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvItemQuantity;
        private TextView tvItemPrice;
        private TextView tvItemSubtotal;
        private ImageButton btnRemoveItem;

        public BillItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvItemSubtotal = itemView.findViewById(R.id.tv_item_subtotal);
            btnRemoveItem = itemView.findViewById(R.id.btn_remove_item);
        }

        public void bind(final BillItem item, final int position) {
            tvItemName.setText(item.getProductName());
            tvItemQuantity.setText(String.valueOf(item.getQuantity()));
            tvItemPrice.setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()));
            tvItemSubtotal.setText(String.format(Locale.getDefault(), "%.2f", item.getSubtotal()));

            btnRemoveItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemove(position);
                }
            });
        }
    }
}
