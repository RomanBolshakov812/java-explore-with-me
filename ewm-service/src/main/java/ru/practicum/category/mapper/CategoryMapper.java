package ru.practicum.category.mapper;

import java.util.ArrayList;
import java.util.List;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        List<CategoryDto> list = new ArrayList<>();
        for (Category category : categories) {
            CategoryDto categoryDto = toCategoryDto(category);
            list.add(categoryDto);
        }
        return list;
    }
}
