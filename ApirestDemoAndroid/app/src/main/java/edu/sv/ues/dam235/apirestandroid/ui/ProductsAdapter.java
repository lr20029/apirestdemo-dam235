package edu.sv.ues.dam235.apirestandroid.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.sv.ues.dam235.apirestandroid.databinding.ItemProductBinding;
import edu.sv.ues.dam235.apirestandroid.model.Product;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    public interface OnProductListener {
        void onEdit(Product product);

        void onDelete(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private OnProductListener listener;

    public void setListener(OnProductListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product, OnProductListener listener) {
            binding.tvName.setText(product.getName());
            String estado = product.isStatus() ? "Sí" : "No";
            binding.tvDetail.setText("Código: " + product.getCode() + " · Activo: " + estado);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(product);
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(product);
                    return true;
                }
                return false;
            });
        }
    }
}
