package ecommerce.Apna_Bazaar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Apna_Bazaar.payload.request.CategoryRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponsePageDTO;
import ecommerce.Apna_Bazaar.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- CREATE CATEGORY ----------------

    @Test
    void createCategory_success() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Electronics");
        requestDTO.setDescription("Electronics items");

        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Electronics");
        responseDTO.setDescription("Electronics items");

        Mockito.when(categoryService.createCategory(Mockito.any(CategoryRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronics items"));
    }

    @Test
    void createCategory_validationFailure() throws Exception {
        CategoryRequestDTO invalidRequest = new CategoryRequestDTO();
        invalidRequest.setDescription("Missing name");

        mockMvc.perform(post("/api/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ---------------- UPDATE CATEGORY ----------------

    @Test
    void updateCategory_success() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Updated Electronics");
        requestDTO.setDescription("Updated description");

        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Updated Electronics");
        responseDTO.setDescription("Updated description");

        Mockito.when(categoryService.updateCategory(Mockito.eq(1L), Mockito.any(CategoryRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/admin/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Electronics"));
    }

    // ---------------- DELETE CATEGORY ----------------

    @Test
    void deleteCategory_success() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/admin/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete Category successfully"));
    }

    // ---------------- GET CATEGORY BY ID ----------------

    @Test
    void getCategoryById_success() throws Exception {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Electronics");
        responseDTO.setDescription("Electronics items");

        Mockito.when(categoryService.getCategoryById(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/public/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    // ---------------- GET ALL CATEGORIES (PAGINATION) ----------------

    @Test
    void getAllCategories_success() throws Exception {
        CategoryResponseDTO category = new CategoryResponseDTO();
        category.setId(1L);
        category.setName("Electronics");

        CategoryResponsePageDTO pageDTO = new CategoryResponsePageDTO();
        pageDTO.setCategories(List.of(category));
        pageDTO.setPageNumber(0);
        pageDTO.setPageSize(10);
        pageDTO.setTotalElements(1L);
        pageDTO.setTotalPages(1);
        pageDTO.setLast(true);

        Mockito.when(categoryService.getAllCategories(
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/public/categories")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .param("sortBy", "name")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].name").value("Electronics"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }
}