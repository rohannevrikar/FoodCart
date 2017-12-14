package com.example.rohannevrikar.googlemaptest;

/**
 * Created by Rohan Nevrikar on 04-12-2017.
 */

public class RestaurantInfo {



    private int id;
    private String restaurantName;
    private String cuisine;
    private String deliveryTime;
    private String priceForTwo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPriceForTwo() {
        return priceForTwo;
    }

    public void setPriceForTwo(String priceForTwo) {
        this.priceForTwo = priceForTwo;
    }
}
