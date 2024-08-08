//package pl.codeleak.demos.sbt.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import pl.codeleak.demos.sbt.model.Category;
//import pl.codeleak.demos.sbt.repository.CategoryRepository;
//
//import java.text.Normalizer;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.regex.Pattern;
//
//@Service
//public class CategoryService {
//
//    private final CategoryRepository categoryRepository;
//    private final ProductService productService;
//
//    @Autowired
//    public CategoryService(CategoryRepository categoryRepository, ProductService productService) {
//        this.categoryRepository = categoryRepository;
//        this.productService = productService;
//    }
//
//    public Iterable<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }
//
//    public Category getCategoryById(int cid) {
//        return categoryRepository.findById(cid).orElse(null);
//    }
//
//    public void saveCategory(Category category) {
//        categoryRepository.save(category);
//    }
//
//    public void deleteCategoryById(int id) {
//        productService.deleteProductsByCategoryId(id);
//        categoryRepository.deleteById(id);
//    }
//
//    public void updateCategory(Category category) {
//        categoryRepository.save(category);
//    }
//
//
//}

package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void save(Category category) {
        categoryRepository.save(category);
    }

    public void update(Category category) {
        Category existingCategory = categoryRepository.findById(category.getCid()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setGroupName(category.getGroupName());
            existingCategory.setCategoryName(category.getCategoryName());
            existingCategory.setDescribe(category.getDescribe());
            categoryRepository.save(existingCategory);
        }
    }

    public void deleteById(int cid) {
        categoryRepository.deleteById(cid);
    }
}

