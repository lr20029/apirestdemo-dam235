package edu.sv.ues.dam235.apirestandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Collections;
import java.util.List;

import edu.sv.ues.dam235.apirestandroid.R;
import edu.sv.ues.dam235.apirestandroid.api.ApiClient;
import edu.sv.ues.dam235.apirestandroid.data.SessionManager;
import edu.sv.ues.dam235.apirestandroid.databinding.ActivityProductsBinding;
import edu.sv.ues.dam235.apirestandroid.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity implements ProductsAdapter.OnProductListener {

    public static final String EXTRA_TOKEN = "extra_token";

    private ActivityProductsBinding binding;
    private ProductsAdapter adapter;
    private SessionManager session;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        token = getIntent().getStringExtra(EXTRA_TOKEN);
        session = new SessionManager(this);
        if (token == null || token.isEmpty()) {
            token = session.getToken();
        }
        if (token == null || token.isEmpty()) {
            goToLogin();
            return;
        }

        adapter = new ProductsAdapter();
        adapter.setListener(this);
        binding.recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerProducts.setAdapter(adapter);

        binding.btnLogout.setOnClickListener(v -> doLogout());
        binding.btnAdd.setOnClickListener(v -> openFormNew());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (token != null && !token.isEmpty()) {
            loadProducts();
        }
    }

    private void openFormNew() {
        Intent intent = new Intent(this, ProductFormActivity.class);
        intent.putExtra(ProductFormActivity.EXTRA_TOKEN, token);
        startActivity(intent);
    }

    @Override
    public void onEdit(Product product) {
        Intent intent = new Intent(this, ProductFormActivity.class);
        intent.putExtra(ProductFormActivity.EXTRA_TOKEN, token);
        intent.putExtra(ProductFormActivity.EXTRA_CODE,
                product.getCode() == null ? -1 : product.getCode());
        intent.putExtra(ProductFormActivity.EXTRA_NAME, product.getName());
        intent.putExtra(ProductFormActivity.EXTRA_STATUS, product.isStatus());
        startActivity(intent);
    }

    @Override
    public void onDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_ok, (dialog, which) -> deleteProduct(product))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteProduct(Product product) {
        if (product.getCode() == null) {
            return;
        }
        setLoading(true);
        ApiClient.getApi().deleteProduct("Bearer " + token, product.getCode())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        setLoading(false);
                        if (response.isSuccessful() || response.code() == 404) {
                            Toast.makeText(ProductsActivity.this,
                                    "Producto eliminado", Toast.LENGTH_SHORT).show();
                            loadProducts();
                        } else {
                            Toast.makeText(ProductsActivity.this,
                                    "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(ProductsActivity.this,
                                "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadProducts() {
        setLoading(true);

        ApiClient.getApi().getProducts("Bearer " + token)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Product>> call,
                                           @NonNull Response<List<Product>> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            adapter.setItems(response.body());
                            binding.tvEmpty.setVisibility(
                                    response.body().isEmpty() ? View.VISIBLE : View.GONE);

                        } else if (response.code() == 204) {
                            adapter.setItems(Collections.emptyList());
                            binding.tvEmpty.setVisibility(View.VISIBLE);

                        } else if (response.code() == 401 || response.code() == 403) {
                            Toast.makeText(ProductsActivity.this,
                                    "Sesión expirada. Inicie sesión de nuevo.",
                                    Toast.LENGTH_SHORT).show();
                            goToLogin();

                        } else {
                            Toast.makeText(ProductsActivity.this,
                                    "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                        setLoading(false);
                        Toast.makeText(ProductsActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void doLogout() {
        setLoading(true);
        ApiClient.getApi().logout("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                goToLogin();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        if (session != null) {
            session.clear();
        }
        Intent intent = new Intent(ProductsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
