package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.error.exception.DependentEntitiesException;
import ru.practicum.event.EventRepository;
import ru.practicum.util.PageMaker;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category with id=" + catId + " was not found"));
        Long countRelatedEvents = eventRepository.countEventsByCategoryId(catId);
        if (countRelatedEvents == 0) {
            categoryRepository.deleteById(catId);
        } else {
            throw new DependentEntitiesException("The category is not empty");
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category currentCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category with id=" + catId + " was not found"));
        currentCategory.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(currentCategory));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll(page).toList();
        if (categoryList.size() == 0) {
            return categoryDtoList;
        } else {
            return CategoryMapper.toCategoryDtoList(categoryList);
        }
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.toCategoryDto(category);
    }
}
