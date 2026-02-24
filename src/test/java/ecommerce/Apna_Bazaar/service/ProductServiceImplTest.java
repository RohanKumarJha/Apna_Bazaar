package ecommerce.Apna_Bazaar.service;

import ecommerce.Apna_Bazaar.exception.ResourceAlreadyExistException;
import ecommerce.Apna_Bazaar.exception.ResourceNotExistException;
import ecommerce.Apna_Bazaar.model.Category;
import ecommerce.Apna_Bazaar.model.Product;
import ecommerce.Apna_Bazaar.payload.request.ProductRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponsePageDTO;
import ecommerce.Apna_Bazaar.repository.CategoryRepository;
import ecommerce.Apna_Bazaar.repository.ProductRepository;
import ecommerce.Apna_Bazaar.service.Impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private FileService fileService;

    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        fileService = mock(FileService.class);

        // REAL
        ModelMapper modelMapper = new ModelMapper(); // IMPORTANT

        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                modelMapper,
                fileService
        );

        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Mobile")
                .description("Smart phone")
                .price(BigDecimal.valueOf(10000))
                .category(category)
                .build();

        requestDTO = ProductRequestDTO.builder()
                .name("Mobile")
                .description("Smart phone")
                .price(BigDecimal.valueOf(10000))
                .categoryId(1L)
                .build();
    }

    // ---------- CREATE ----------

    @Test
    void createProduct_success() {
        when(productRepository.existsByName("Mobile")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response =
                productService.createProduct(requestDTO);

        assertNotNull(response);
        assertEquals("Mobile", response.getName());
    }

    @Test
    void createProduct_duplicate_shouldFail() {
        when(productRepository.existsByName("Mobile")).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
                () -> productService.createProduct(requestDTO));
    }

    // ---------- UPDATE ----------

    @Test
    void updateProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("Mobile")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(product);

        ProductResponseDTO response =
                productService.updateProduct(1L, requestDTO);

        assertEquals("Mobile", response.getName());
    }

    @Test
    void updateProduct_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotExistException.class,
                () -> productService.updateProduct(1L, requestDTO));
    }

    // ---------- DELETE ----------

    @Test
    void deleteProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotExistException.class,
                () -> productService.deleteProduct(1L));
    }

    // ---------- GET BY ID ----------

    @Test
    void getProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response =
                productService.getProductById(1L);

        assertEquals("Mobile", response.getName());
    }

    @Test
    void getProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotExistException.class,
                () -> productService.getProductById(1L));
    }

    // ---------- GET ALL ----------

    @Test
    void getAllProducts_success() {
        Page<Product> page = new PageImpl<>(List.of(product));

        when(fileService.pageSort(any(), any())).thenReturn(Sort.by("id"));
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        ProductResponsePageDTO result =
                productService.getAllProducts(0, 5, "id", "asc");

        assertEquals(1, result.getProducts().size());
    }

    @Test
    void getAllProductsByCategory_success() {
        Page<Product> page = new PageImpl<>(List.of(product));

        when(fileService.pageSort(any(), any())).thenReturn(Sort.by("id"));
        when(productRepository.findByCategoryId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        ProductResponsePageDTO result =
                productService.getAllProductsByCategoryId(1L, 0, 5, "id", "asc");

        assertEquals(1, result.getProducts().size());
    }
}