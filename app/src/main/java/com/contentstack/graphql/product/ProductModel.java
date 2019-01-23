package com.contentstack.graphql.product;

public class ProductModel {
    private String title;
    private double price;
    private String url;

    public ProductModel(String title, double price, String url) {
        this.title = title;
        this.price = price;
        this.url = url;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}
