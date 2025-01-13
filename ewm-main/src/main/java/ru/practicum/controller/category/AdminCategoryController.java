package ru.practicum.controller.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.addNew(categoryDto);
    }

    @PatchMapping("/{category_id}")
    public CategoryDto updateCategory(@Positive @PathVariable("category_id") Long categoryId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(categoryId);
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{category_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable("category_id") Long categoryId) {
        categoryService.delete(categoryId);
    }
}
