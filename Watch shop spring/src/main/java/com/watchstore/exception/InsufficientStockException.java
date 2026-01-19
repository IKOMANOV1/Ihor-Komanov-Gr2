package com.watchstore.exception;

public class InsufficientStockException extends RuntimeException {
    private final Long watchId;
    private final String watchName;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(Long watchId, String watchName, int availableStock, int requestedQuantity) {
        super("Not enough stock for: " + watchName);
        this.watchId = watchId;
        this.watchName = watchName;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getWatchId() {
        return watchId;
    }

    public String getWatchName() {
        return watchName;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
