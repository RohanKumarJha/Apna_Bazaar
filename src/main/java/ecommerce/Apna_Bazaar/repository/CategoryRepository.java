package ecommerce.Apna_Bazaar.repository;

import ecommerce.Apna_Bazaar.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String newName);
}
