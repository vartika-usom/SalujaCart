package com.usomandroidproject.salujaecommerce;

public class Order {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(int totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    private int totalAmountPaid;
    private int id;
    private String area;
    private String orderDate;
    private String deliveryDate;
    private String fullName;
    private String houseNo;
    private String landmark;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getTownCity() {
        return townCity;
    }

    public void setTownCity(String townCity) {
        this.townCity = townCity;
    }

    public String getState() {
        return state;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Order(int id, String area, String orderDate, String deliveryDate, String fullName, String houseNo,
                 String landmark, String pinCode, String townCity, String state, String orderNumber, int totalAmountPaid) {
        this.id = id;
        this.area = area;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.fullName = fullName;
        this.houseNo = houseNo;
        this.landmark = landmark;
        this.pinCode = pinCode;
        this.townCity = townCity;
        this.state = state;
        this.orderNumber = orderNumber;
        this.totalAmountPaid = totalAmountPaid;
    }

    private String pinCode;
    private String townCity;
    private String state;
    private String orderNumber;


}
