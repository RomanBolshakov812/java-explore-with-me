package ru.practicum.category;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;

@Transactional
public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long catId);

    @Transactional(readOnly = true)
    List<CategoryDto> getCategories(Integer from, Integer size);

    @Transactional(readOnly = true)
    CategoryDto getCategoryById(Long catId);
}
