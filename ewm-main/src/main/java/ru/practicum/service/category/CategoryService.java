package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    public CategoryDto addNew(CategoryDto categoryDto);

    public CategoryDto update(CategoryDto categoryDto);

    public void delete(Integer categoryId);

    public List<CategoryDto> getAll(Integer from, Integer size);

    public CategoryDto getById(Integer categoryId);

}
