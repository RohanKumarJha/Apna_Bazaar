package ecommerce.Apna_Bazaar.service;

import ecommerce.Apna_Bazaar.payload.request.CategoryRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponsePageDTO;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO);
    void deleteCategory(Long id);
    CategoryResponseDTO getCategoryById(Long id);
    CategoryResponsePageDTO getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
}
