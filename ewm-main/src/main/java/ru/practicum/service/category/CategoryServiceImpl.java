package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.util.ErrorMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addNew(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictPropertyConstraintException(String.format("Категория с именем %s уже существует",
                    categoryDto.getName()));
        }
        final Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto) {
        final Category category = findById(categoryDto.getId());
        final String updatedName = categoryDto.getName();
        categoryRepository.findByName(updatedName).map(Category::getId)
                .ifPresent(categoryId -> {
                    if (!categoryId.equals(categoryDto.getId())) {
                        throw new ConflictPropertyConstraintException(String.format("Категория с именем %s" +
                                        " уже существует", updatedName));
                    }
                });
        category.setName(updatedName);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(0, size + from, Sort.by("id").ascending());
        return categoryRepository.findAll(pageable).stream()
                .skip(from)
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        return categoryMapper.toCategoryDto(findById(categoryId));
    }

    private Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.CategoryNotFoundMessage(categoryId)));
    }
}
