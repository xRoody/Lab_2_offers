package com.example.my.DTOs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;



public class PayMethod {
    private Long id;
    private String title;
    private String data;
    private Long payId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter(value = "title")
    public String getTitle() {
        return title;
    }

    @JsonSetter(value = "name")
    public void setTitle(String title) {
        this.title = title;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getPayId() {
        return payId;
    }

    public void setPayId(Long payId) {
        this.payId = payId;
    }
}
