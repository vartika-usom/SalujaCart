package com.usomandroidproject.salujaecommerce;

public class PaymentModel {

    private String PaymentType;

    private double Amount;

    private String Status;

    private String FirstName ;

    private String TaxId;

    private String Hash;

    private String ProductInfo;

    private String Mobile;

    public PaymentModel(String paymentType, double amount, String status, String firstName, String taxId,
                        String hash, String productInfo, String mobile, String email, String payuMoneyId) {

        PaymentType = paymentType;
        Amount = amount;
        Status = status;
        FirstName = firstName;
        TaxId = taxId;
        Hash = hash;
        ProductInfo = productInfo;
        Mobile = mobile;
        Email = email;
        PayuMoneyId = payuMoneyId;
    }

    private String Email;

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getTaxId() {
        return TaxId;
    }

    public void setTaxId(String taxId) {
        TaxId = taxId;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public String getProductInfo() {
        return ProductInfo;
    }

    public void setProductInfo(String productInfo) {
        ProductInfo = productInfo;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPayuMoneyId() {
        return PayuMoneyId;
    }

    public void setPayuMoneyId(String payuMoneyId) {
        PayuMoneyId = payuMoneyId;
    }

    private String PayuMoneyId;
}
