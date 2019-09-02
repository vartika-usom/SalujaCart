package com.usomandroidproject.salujaecommerce;

public class Brand {

    private int id;

    private String title;

    private String ImgUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public Brand(int id, String title, String imgUrl) {
        this.id = id;
        this.title = title;
        ImgUrl = imgUrl;
    }
}
