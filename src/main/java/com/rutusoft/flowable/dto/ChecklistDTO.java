package com.rutusoft.flowable.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistDTO {

    private Integer totalItems;
    private List<Category> categories;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category {
        private String categoryName;
        private List<Item> items;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String question;
        private Boolean mandatory;
        private String variable;
    }
}