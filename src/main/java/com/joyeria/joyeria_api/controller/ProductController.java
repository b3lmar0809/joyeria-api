package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.service.ProductService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Product>> getRecentProducts() {
        List<Product> products = productService.getRecentProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/sale")
    public ResponseEntity<List<Product>> getProductsOnSale() {
        List<Product> products = productService.getProductsOnSale();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<Product>> getProductsByMaterial(@PathVariable Long materialId) {
        List<Product> products = productService.getProductsByMaterial(materialId);
        return ResponseEntity.ok(products);
    }

    // @RequestParam extrae parámetros de la query string
    // /api/products/search?keyword=anillo → keyword = "anillo"
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProductsByName(keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max
    ) {
        List<Product> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    //filtrar productos por multiples criterios
    // required = false hace que el parametro sea opcional
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<Product> products = productService.getProductsByFilters(
                categoryId, materialId, minPrice, maxPrice
        );
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product
    ) {
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Product product
    ) {
        Product updated = productService.partialUpdateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request
    ) {
        Product updated = productService.updateStock(id, request.getStock());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Product> updatePrice(
            @PathVariable Long id,
            @RequestBody PriceUpdateRequest request
    ) {
        Product updated = productService.updatePrice(id, request.getPrice());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    //DTOs internos para requests especificos
    // clase interna para actualización de stock
    private static class StockUpdateRequest {
        private Integer stock;

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    // clase interna para actualización de precio
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