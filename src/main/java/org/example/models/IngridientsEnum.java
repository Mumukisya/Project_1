package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IngridientsEnum {
        FLUORESCENT_BUN("61c0c5a71d1f82001bdaaa6d", "Флюоресцентная булка R2-D3", "bun"),
        CRATER_BUN("61c0c5a71d1f82001bdaaa6c", "Краторная булка N-200i", "bun"),
        SPICY_X_SAUCE("61c0c5a71d1f82001bdaaa72", "Соус Spicy-X", "sauce"),
        SPACE_SAUCE("61c0c5a71d1f82001bdaaa73", "Соус фирменный Space Sauce", "sauce"),
        GALAXY_SAUCE("61c0c5a71d1f82001bdaaa74", "Соус традиционный галактический", "sauce"),
        SPIKED_SAUCE("61c0c5a71d1f82001bdaaa75", "Соус с шипами Антарианского плоскоходца", "sauce"),
        PROTOSTOMIA("61c0c5a71d1f82001bdaaa6f", "Мясо бессмертных моллюсков Protostomia", "main"),
        BEEF_METEOR("61c0c5a71d1f82001bdaaa70", "Говяжий метеорит (отбивная)", "main"),
        TETRAODONTIMFORM_FILLET("61c0c5a71d1f82001bdaaa6e", "Филе Люминесцентного тетраодонтимформа", "main"),
        BIO_PATTIE("61c0c5a71d1f82001bdaaa71", "Биокотлета из марсианской Магнолии", "main"),
        CRUSTY_CIRCLES("61c0c5a71d1f82001bdaaa76", "Хрустящие минеральные кольца", "main"),
        FALLENIAN_TREE_FRUITS("61c0c5a71d1f82001bdaaa77", "Плоды Фалленианского дерева", "main"),
        MARS_CRYSTALS("61c0c5a71d1f82001bdaaa78", "Кристаллы марсианских альфа-сахаридов", "main"),
        MINI_SALAD("61c0c5a71d1f82001bdaaa79", "Мини-салат Экзо-Плантаго", "main"),
        CHEESE("61c0c5a71d1f82001bdaaa7a", "Сыр с астероидной плесенью", "main");


        private final String id;
        private final String name;
        private final String type;
}
