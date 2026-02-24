package ecommerce.Apna_Bazaar.service.Impl;

import ecommerce.Apna_Bazaar.exception.ResourceAlreadyExistException;
import ecommerce.Apna_Bazaar.exception.ResourceNotExistException;
import ecommerce.Apna_Bazaar.model.Category;
import ecommerce.Apna_Bazaar.payload.request.CategoryRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponsePageDTO;
import ecommerce.Apna_Bazaar.repository.CategoryRepository;
import ecommerce.Apna_Bazaar.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final String CACHE_NAME = "categories";
    private final FileServiceImpl fileService;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        categoryExistByName(categoryRequestDTO.getName());
        Category category = modelMapper.map(categoryRequestDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = findCategoryById(id);
        String existingName = category.getName();
        String newName = dto.getName();
        if (!existingName.equalsIgnoreCase(newName)) {
            categoryExistByName(newName);
            category.setName(newName);
        }
        category.setDescription(dto.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category findCategory = findCategoryById(id);
        categoryRepository.delete(findCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        return modelMapper.map(categoryRepository.findById(id), CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponsePageDTO getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, fileService.pageSort(sortBy, sortDir));
        Page<Category> page = categoryRepository.findAll(pageable);
        List<Category> categories = page.getContent();
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        for (Category category : categories) {
            categoryResponseDTOS.add(modelMapper.map(category, CategoryResponseDTO.class));
        }
        return categoryResponseDTO_To_CategoryResponsePageDTO(categoryResponseDTOS, page);
    }

    //    ---------------------------------- Helper methods -------------------------------------------
    private void categoryExistByName(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistException("Category already exist with categoryName " + name);
        }
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotExistException("Category not exist with categoryId " + id));
    }

    private CategoryResponsePageDTO categoryResponseDTO_To_CategoryResponsePageDTO(
            List<CategoryResponseDTO> categoryResponseDTOS, Page<Category> page) {
        CategoryResponsePageDTO categoryResponsePageDTO = new CategoryResponsePageDTO();
        categoryResponsePageDTO.setCategories(categoryResponseDTOS);
        categoryResponsePageDTO.setPageNumber(page.getNumber());
        categoryResponsePageDTO.setPageSize(page.getSize());
        categoryResponsePageDTO.setTotalPages(page.getTotalPages());
        categoryResponsePageDTO.setTotalElements(page.getTotalElements());
        categoryResponsePageDTO.setLast(page.isLast());
        return categoryResponsePageDTO;
    }
}
