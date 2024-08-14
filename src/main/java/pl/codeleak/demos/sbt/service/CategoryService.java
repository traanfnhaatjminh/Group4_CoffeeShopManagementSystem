

package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

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
        productService.deleteProductsByCategoryId(cid);
        categoryRepository.deleteById(cid);
    }
    public Page<Category> findPaginated(int pageNo, int pageSize) {
        return categoryRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
    }
    public Page<Category> searchByName(String keyword, int pageNo, int pageSize) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword, PageRequest.of(pageNo - 1, pageSize));
    }
}

