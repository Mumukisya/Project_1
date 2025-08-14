package org.example.generators;

import com.github.javafaker.Faker;
import org.example.models.CreateOrderRequest;
import org.example.models.IngridientsEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OrderGenerator {
    private static final Random random = new Random();

    public static CreateOrderRequest generateOrder(int bunCount, int sauceCount, int mainCount) {
        if (bunCount < 0 || sauceCount < 0 || mainCount < 0) {
            throw new IllegalArgumentException("Количество ингредиентов не может быть отрицательным");
        }
        List<String> ingredients = new ArrayList<>();
        if (bunCount == 0 && sauceCount != 0 && mainCount != 0) {
            ingredients.add("0");
            addIngredients(ingredients, "sauce", sauceCount);
            addIngredients(ingredients, "main", mainCount);
        }else if (bunCount == 0 && sauceCount == 0 && mainCount == 0) {
            addIngredients(ingredients, "bun", bunCount);
            addIngredients(ingredients, "sauce", sauceCount);
            addIngredients(ingredients, "main", mainCount);
        }
        else {
            addIngredients(ingredients, "bun", bunCount);
            addIngredients(ingredients, "sauce", sauceCount);
            addIngredients(ingredients, "main", mainCount);
        }

        return new CreateOrderRequest().setIngredients(ingredients);
    }

    public static CreateOrderRequest generateNonValidOrder(int ingridientsCount) {
        Faker faker = new Faker();
        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < ingridientsCount; i++) {
            ingredients.add(faker.regexify("[A-Za-z0-9]{24}"));
        }
        return new CreateOrderRequest().setIngredients(ingredients);
    }


    public static CreateOrderRequest generateRandomValidOrder() {
        return generateOrder(
                1,                      // 1 булка
                1 + random.nextInt(2),   // 1-2 соуса
                1 + random.nextInt(3)    // 1-3 начинки
        );
    }

    private static void addIngredients(List<String> ingredients, String type, int count) {
        List<String> available = getIngredientIdsByType(type);
        if (!available.isEmpty()) {
            for (int i = 0; i < count; i++) {
                ingredients.add(getRandomElement(available));
            }
        }
    }

    private static List<String> getIngredientIdsByType(String type) {
        return Arrays.stream(IngridientsEnum.values())
                .filter(i -> i.getType().equals(type))
                .map(IngridientsEnum::getId)
                .collect(Collectors.toList());
    }

    private static String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
