package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import java.util.List;

public interface ProductServices {
    public List<ProductsDTO> getAllProducts();

    public ProductsDTO getProductById(Integer code);

    public ProductsDTO createProduct(ProductsDTO request);

    public ProductsDTO updateProduct(Integer code, ProductsDTO request);

    public void deleteProduct(Integer code);
}
