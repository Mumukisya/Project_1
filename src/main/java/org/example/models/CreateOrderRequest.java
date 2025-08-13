package org.example.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CreateOrderRequest {
    private List<String> ingredients;
}
