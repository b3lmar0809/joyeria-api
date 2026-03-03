package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.exception.DuplicateResourceException;
import com.joyeria.joyeria_api.exception.InsufficientStockException;
import com.joyeria.joyeria_api.exception.InvalidOperationException;
import com.joyeria.joyeria_api.exception.ResourceNotFoundException;
import com.joyeria.joyeria_api.model.Category;
import com.joyeria.joyeria_api.model.Material;
import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
/**
 * ProductService class
 *
 * @Version: 1.0.1- 02 mar. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 - 15 feb. 2026
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final MaterialService materialService;


    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getRecentProducts() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsOnSale() {
        return productRepository.findProductsOnSale();
    }


    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "SKU", sku));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return productRepository.findByCategoryAndActiveTrue(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByMaterial(Long materialId) {
        Material material = materialService.getMaterialById(materialId);
        return productRepository.findByMaterialAndActiveTrue(material);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByFilters(
            Long categoryId,
            Long materialId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return productRepository.findByFilters(categoryId, materialId, minPrice, maxPrice);
    }

    public Product partialUpdateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getPrice() != null) {
            if (productDetails.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOperationException("El precio debe ser mayor a 0");
            }
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getStock() != null) {
            if (productDetails.getStock() < 0) {
                throw new InvalidOperationException("El stock no puede ser negativo");
            }
            product.setStock(productDetails.getStock());
        }
        if (productDetails.getSku() != null) {
            if (!product.getSku().equals(productDetails.getSku()) &&
                    productRepository.existsBySku(productDetails.getSku())) {
                throw new DuplicateResourceException("Product", "SKU", productDetails.getSku());
            }
            product.setSku(productDetails.getSku());
        }
        if (productDetails.getWeight() != null) {
            product.setWeight(productDetails.getWeight());
        }
        if (productDetails.getDimensions() != null) {
            product.setDimensions(productDetails.getDimensions());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getMaterial() != null) {
            product.setMaterial(productDetails.getMaterial());
        }
        if (productDetails.getGemstones() != null) {
            product.setGemstones(productDetails.getGemstones());
        }
        if (productDetails.getImages() != null && !productDetails.getImages().isEmpty()) {
            product.setImages(productDetails.getImages());
        }
        if (productDetails.getFeatured() != null) {
            product.setFeatured(productDetails.getFeatured());
        }
        if (productDetails.getDiscountPrice() != null) {
            if (productDetails.getDiscountPrice().compareTo(product.getPrice()) >= 0) {
                throw new InvalidOperationException(
                        "El precio con descuento debe ser menor al precio original"
                );
            }
            product.setDiscountPrice(productDetails.getDiscountPrice());
        }
        if (productDetails.getActive() != null) {
            product.setActive(productDetails.getActive());
        }

        return productRepository.save(product);
    }

    public Product updateStock(Long id, Integer newStock) {
        Product product = getProductById(id);

        if (newStock < 0) {
            throw new InvalidOperationException("El stock no puede ser negativo");
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    public Product updatePrice(Long id, BigDecimal newPrice) {
        Product product = getProductById(id);

        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("El precio debe ser mayor a 0");
        }

        product.setPrice(newPrice);
        return productRepository.save(product);
    }



    //obtiene todos los productos con paginacion
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    //buscar productos con paginacion
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    //obtiene productos destacados con paginacion
    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByFeaturedTrueAndActiveTrue(pageable);
    }

    public Page<Product> getProductsOnSale(Pageable pageable) {
        return productRepository.findProductsOnSale(pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setSku(productDetails.getSku());
        product.setCategory(productDetails.getCategory());
        product.setMaterial(productDetails.getMaterial());
        product.setWeight(productDetails.getWeight());
        product.setDimensions(productDetails.getDimensions());
        product.setGemstones(productDetails.getGemstones());
        product.setImages(productDetails.getImages());
        product.setFeatured(productDetails.getFeatured());
        product.setDiscountPrice(productDetails.getDiscountPrice());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    //reducir stock
    public void reduceStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStock(), quantity);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    //aumentar stock
    public void increaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}