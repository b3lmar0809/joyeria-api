package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
/**
 * ProductController class
 *
 * @Version: 1.0.1- 02 mar. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 - 22 feb. 2026
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


     //obtiene todos los productos con paginacion
     //ej: GET /api/products?page=0&size=20&sort=price&direction=DESC
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<Product> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(products);
    }

    //buscar productos por palabra clave con paginacion
     //ej: GET /api/products/search?keyword=anillo&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<Product> products = productService.searchProducts(keyword, pageable);

        return ResponseEntity.ok(products);
    }

     //GET /api/products/category/{categoryId}
     //obtiene productos por categoría con paginacion
     //ej GET /api/products/category/1?page=0&size=10

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<Product> products = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(products);
    }



    //GET /api/products/featured
    //obtiene productos destacados con paginacion
    @GetMapping("/featured")
    public ResponseEntity<Page<Product>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getFeaturedProducts(pageable);

        return ResponseEntity.ok(products);
    }

    //GET /api/products/sale
    //obtiene productos en oferta con paginacion
    @GetMapping("/sale")
    public ResponseEntity<Page<Product>> getProductsOnSale(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsOnSale(pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/all-active")
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Product>> getRecentProducts() {
        List<Product> products = productService.getRecentProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<Product>> getProductsByMaterial(@PathVariable Long materialId) {
        List<Product> products = productService.getProductsByMaterial(materialId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Product> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByFilters(
                categoryId, materialId, minPrice, maxPrice
        );
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        Product updated = productService.partialUpdateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request) {
        Product updated = productService.updateStock(id, request.getStock());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Product> updatePrice(
            @PathVariable Long id,
            @RequestBody PriceUpdateRequest request) {
        Product updated = productService.updatePrice(id, request.getPrice());
        return ResponseEntity.ok(updated);
    }

    private static class StockUpdateRequest {
        private Integer stock;

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    private static class PriceUpdateRequest {
        private BigDecimal price;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}