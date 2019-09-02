package com.usomandroidproject.salujaecommerce;

public class Product {
    private String name;

    private double price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private double offerPrice;

    private double perOff;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public double getPerOff() {
        return perOff;
    }

    public void setPerOff(double perOff) {
        this.perOff = perOff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconImageFullPath() {
        return IconImageFullPath;
    }

    public void setIconImageFullPath(String iconImageFullPath) {
        IconImageFullPath = iconImageFullPath;
    }

    private int ProductCategoryId;

    private String ProductCategoryName;

    public int getProductCategoryId() {
        return ProductCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        ProductCategoryId = productCategoryId;
    }

    public String getProductCategoryName() {
        return ProductCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        ProductCategoryName = productCategoryName;
    }

    private String Description;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getNumberOfProductInStock() {
        return NumberOfProductInStock;
    }

    public void setNumberOfProductInStock(int numberOfProductInStock) {
        NumberOfProductInStock = numberOfProductInStock;
    }

    private int NumberOfProductInStock;

    private String IconImageFullPath;

    public Product()
    {}

    public Product(String name, double price, int id, double offerPrice, double perOff, int quantity,
                   int productCategoryId, String productCategoryName, String iconImageFullPath, int numberOfProductInStock
    , String description) {
        this.name = name;
        this.price = price;
        this.id = id;
        this.offerPrice = offerPrice;
        this.perOff = perOff;
        this.quantity = quantity;
        ProductCategoryId = productCategoryId;
        ProductCategoryName = productCategoryName;
        IconImageFullPath = iconImageFullPath;
        NumberOfProductInStock = numberOfProductInStock;
        Description = description;
    }

    public static class Cart extends Product
    {

        public int getCartId() {
            return CartId;
        }

        public void setCartId(int cartId) {
            CartId = cartId;
        }

        private int CartId;

        public Cart(int CartId ,String name, double price, int id, double offerPrice, double perOff, int quantity, int productCategoryId,
                    String productCategoryName, String iconImageFullPath, int numberOfProductInStock, String description) {
            super(name, price, id, offerPrice, perOff, quantity, productCategoryId, productCategoryName, iconImageFullPath, numberOfProductInStock, description);
            this.CartId = CartId;
        }

        public Cart()
        {

        }
    }
}
