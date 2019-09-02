package com.usomandroidproject.salujaecommerce;

public class OrderItems {

    private String title;
    private int orderId;
    private double actualPrice;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    private boolean isDelivered;

    private String imageUrl;

    public OrderItems(String title, int orderId, double actualPrice,
                      double discountPrice, int quantity, String color, String deliveryDate, String imageUrl, boolean isDelivered) {
        this.title = title;
        this.orderId = orderId;
        this.actualPrice = actualPrice;
        this.discountPrice = discountPrice;
        this.quantity = quantity;
        this.color = color;
        this.deliveryDate = deliveryDate;
        this.imageUrl = imageUrl;
        this.isDelivered = isDelivered;
    }

    private double discountPrice;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    private int quantity;
    private String color;
    private String deliveryDate;
}
