package ecommerce.Apna_Bazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Apna_Bazaar.payload.request.ProductRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponsePageDTO;
import ecommerce.Apna_Bazaar.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- CREATE PRODUCT ----------------

    @Test
    void createProduct_success() throws Exception {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("79999"))
                .categoryId(1L)
                .build();

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("iPhone 15");

        Mockito.when(productService.createProduct(Mockito.any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("iPhone 15"));
    }

    @Test
    void createProduct_validationFailure_missingFields() throws Exception {
        ProductRequestDTO invalidDTO = ProductRequestDTO.builder()
                .price(new BigDecimal("1000"))
                .build();

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    // ---------------- UPDATE PRODUCT ----------------

    @Test
    void updateProduct_success() throws Exception {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Updated iPhone")
                .price(new BigDecimal("89999"))
                .categoryId(1L)
                .build();

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Updated iPhone");

        Mockito.when(productService.updateProduct(Mockito.eq(1L), Mockito.any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/admin/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated iPhone"));
    }

    // ---------------- DELETE PRODUCT ----------------

    @Test
    void deleteProduct_success() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/admin/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    // ---------------- UPDATE IMAGE ----------------

    @Test
    void updateProductImage_success() throws Exception {
        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "product.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "image-content".getBytes()
                );

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("iPhone");

        Mockito.when(productService.updateImage(Mockito.eq(1L), Mockito.any()))
                .thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/admin/products/{id}/image", 1L)
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // ---------------- GET PRODUCT BY ID ----------------

    @Test
    void getProductById_success() throws Exception {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("iPhone");

        Mockito.when(productService.getProductById(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/public/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone"));
    }

    // ---------------- GET ALL PRODUCTS ----------------

    @Test
    void getAllProducts_success() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO();
        product.setId(1L);
        product.setName("iPhone");

        ProductResponsePageDTO pageDTO = new ProductResponsePageDTO();
        pageDTO.setProducts(List.of(product));
        pageDTO.setPageNumber(0);
        pageDTO.setPageSize(10);
        pageDTO.setTotalElements(1L);
        pageDTO.setTotalPages(1);
        pageDTO.setLast(true);

        Mockito.when(productService.getAllProducts(
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/public/products")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("iPhone"));
    }

    // ---------------- GET PRODUCTS BY CATEGORY ----------------

    @Test
    void getProductsByCategory_success() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO();
        product.setId(1L);
        product.setName("iPhone");

        ProductResponsePageDTO pageDTO = new ProductResponsePageDTO();
        pageDTO.setProducts(List.of(product));
        pageDTO.setTotalElements(1L);

        Mockito.when(productService.getAllProductsByCategoryId(
                        Mockito.eq(1L),
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/public/products/category/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("iPhone"));
    }
}