package ecommerce.Apna_Bazaar.repository;

import ecommerce.Apna_Bazaar.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
