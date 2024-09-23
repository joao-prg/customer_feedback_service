package com.joaogoncalves.feedback.model;

import org.jetbrains.annotations.NotNull;

public class FeedbackCreate {
    @NotNull
    private int rating;
    private String feedback;
    private String customer;
    @NotNull
    private String product;
    @NotNull
    private String vendor;

    public FeedbackCreate() {}

    public FeedbackCreate(@NotNull int rating, String feedback, String customer, @NotNull String product, @NotNull String vendor) {
        this.rating = rating;
        this.feedback = feedback;
        this.customer = customer;
        this.product = product;
        this.vendor = vendor;
    }

    @NotNull
    public int getRating() {
        return rating;
    }

    public void setRating(@NotNull int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public @NotNull String getProduct() {
        return product;
    }

    public void setProduct(@NotNull String product) {
        this.product = product;
    }

    public @NotNull String getVendor() {
        return vendor;
    }

    public void setVendor(@NotNull String vendor) {
        this.vendor = vendor;
    }
}

