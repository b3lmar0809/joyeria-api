package com.joyeria.joyeria_api.exception;

//  excepcion cuando no hay suficiente stock (400)
public class InsufficientStockException extends RuntimeException {

    private Long productId;
    private String productName;
    private Integer available;
    private Integer requested;

    public InsufficientStockException(String productName, Integer available, Integer requested) {
        super(String.format(
                "Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                productName, available, requested
        ));
        this.productName = productName;
        this.available = available;
        this.requested = requested;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getAvailable() {
        return available;
    }

    public Integer getRequested() {
        return requested;
    }
}