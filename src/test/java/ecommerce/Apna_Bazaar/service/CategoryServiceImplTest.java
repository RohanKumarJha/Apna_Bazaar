package ecommerce.Apna_Bazaar.service;

import ecommerce.Apna_Bazaar.exception.ResourceAlreadyExistException;
import ecommerce.Apna_Bazaar.exception.ResourceNotExistException;
import ecommerce.Apna_Bazaar.model.Category;
import ecommerce.Apna_Bazaar.payload.request.CategoryRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponsePageDTO;
import ecommerce.Apna_Bazaar.repository.CategoryRepository;
import ecommerce.Apna_Bazaar.service.Impl.CategoryServiceImpl;
import ecommerce.Apna_Bazaar.service.Impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileServiceImpl fileService;

    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper(); // ✅ REAL INSTANCE

        categoryService = new CategoryServiceImpl(
                categoryRepository,
                modelMapper,
                fileService
        );

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronics items");

        requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Electronics");
        requestDTO.setDescription("Electronics items");
    }

    // ---------------- CREATE CATEGORY ----------------

    @Test
    void createCategory_success() {
        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponseDTO result =
                categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_alreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase("Electronics"))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
                () -> categoryService.createCategory(requestDTO));
    }

    // ---------------- UPDATE CATEGORY ----------------

    @Test
    void updateCategory_success() {
        CategoryRequestDTO updateDTO = new CategoryRequestDTO();
        updateDTO.setName("Updated");
        updateDTO.setDescription("Updated desc");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Updated"))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponseDTO result =
                categoryService.updateCategory(1L, updateDTO);

        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_notFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotExistException.class,
                () -> categoryService.updateCategory(1L, requestDTO));
    }

    // ---------------- DELETE CATEGORY ----------------

    @Test
    void deleteCategory_success() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_notFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotExistException.class,
                () -> categoryService.deleteCategory(1L));
    }

    // ---------------- GET CATEGORY BY ID ----------------

    @Test
    void getCategoryById_success() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        CategoryResponseDTO result =
                categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    // ---------------- GET ALL CATEGORIES ----------------

    @Test
    void getAllCategories_success() {
        Page<Category> page = new PageImpl<>(List.of(category));

        when(fileService.pageSort(anyString(), anyString()))
                .thenReturn(Sort.by("name"));
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        CategoryResponsePageDTO result =
                categoryService.getAllCategories(0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(1, result.getCategories().size());
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isLast());
    }
}