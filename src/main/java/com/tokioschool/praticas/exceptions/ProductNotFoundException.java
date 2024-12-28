package com.tokioschool.praticas.exceptions;

public class ProductNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "The product with id: %d does not exist.";

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super(String.format(DEFAULT_MESSAGE, productId));
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
