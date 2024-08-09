package pl.codeleak.demos.sbt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.repository.CategoryRepository;
import pl.codeleak.demos.sbt.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        Category category1 = new Category(1, "Group1", "Category1", "Description1");
        Category category2 = new Category(2, "Group2", "Category2", "Description2");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Group1", categories.get(0).getGroupName());
        assertEquals("Category2", categories.get(1).getCategoryName());
    }

    @Test
    void testSaveCategory() {
        Category category = new Category(1, "Group1", "Category1", "Description1");
        when(categoryRepository.save(category)).thenReturn(category);

        categoryService.save(category);

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategory() {
        Category existingCategory = new Category(1, "OldGroup", "OldCategory", "OldDescription");
        Category updatedCategory = new Category(1, "NewGroup", "NewCategory", "NewDescription");
        when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        categoryService.update(updatedCategory);

        assertEquals("NewGroup", existingCategory.getGroupName());
        assertEquals("NewCategory", existingCategory.getCategoryName());
        assertEquals("NewDescription", existingCategory.getDescribe());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        Category category = new Category(1, "Group", "Category", "Description");
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        categoryService.update(category);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteById() {
        int categoryId = 1;

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}

