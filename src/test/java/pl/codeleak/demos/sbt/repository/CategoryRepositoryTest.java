package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.codeleak.demos.sbt.model.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll(); // Đảm bảo cơ sở dữ liệu sạch trước mỗi test
    }

    @Test
    void testSaveCategory() {
        Category category = new Category("Cà Phê", "Cà phê máy", "Các loại đồ uống từ espresso với một chút biến tấu hiện đại, hoàn hảo cho những người yêu thích cà phê.");
        category = categoryRepository.save(category);

        assertThat(category.getCid()).isGreaterThan(0);
    }

    @Test
    void testFindAllCategories() {
        categoryRepository.save(new Category("Cà Phê", "Cà phê máy", "Các loại đồ uống từ espresso với một chút biến tấu hiện đại, hoàn hảo cho những người yêu thích cà phê."));
        categoryRepository.save(new Category("Cà Phê", "Cold brew", "Cà phê pha lạnh mịn màng và sảng khoái với hương vị dịu nhẹ, đặc biệt."));

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(2);
    }
}
