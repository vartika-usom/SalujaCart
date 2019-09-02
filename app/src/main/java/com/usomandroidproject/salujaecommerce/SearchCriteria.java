package com.usomandroidproject.salujaecommerce;

public class SearchCriteria {
    private int id;

    public SearchCriteria(int id, String title, String type) {
        this.id = id;
        this.title = title;
        Type = type;
    }

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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    private String title;
    private String Type;
}
