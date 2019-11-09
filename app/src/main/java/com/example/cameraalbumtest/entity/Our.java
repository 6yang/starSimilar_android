package com.example.cameraalbumtest.entity;

public class Our {
    public String name ;
    public String url;

    public Our() {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Our{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
