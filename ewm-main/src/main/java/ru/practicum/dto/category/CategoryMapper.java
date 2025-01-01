package ru.practicum.dto.category;

import org.mapstruct.Mapper;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    public Category toCategory(CategoryDto categoryDto);

    public CategoryDto toCategoryDto(Category category);
}
