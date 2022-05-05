package com.ya;
import lombok.*;
import java.util.ArrayList;
@Data
@Builder
public class Order {
    private ArrayList ingredients;
    public Order(ArrayList ingredients) {
        this.ingredients = ingredients;
    }
}