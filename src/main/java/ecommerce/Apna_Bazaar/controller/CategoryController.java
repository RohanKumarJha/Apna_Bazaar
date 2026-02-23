package ecommerce.Apna_Bazaar.controller;

import ecommerce.Apna_Bazaar.payload.request.CategoryRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.CategoryResponsePageDTO;
import ecommerce.Apna_Bazaar.service.CategoryService;
import ecommerce.Apna_Bazaar.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

//    private Logger log = LogFactory.getLog(CategoryController.class);

    private final CategoryService categoryService;

    // ----------- ADMIN -----------
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO =
                categoryService.createCategory(categoryRequestDTO);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(id, categoryRequestDTO);
        return new ResponseEntity<>(categoryResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete Category successfully");
    }

    // ----------- PUBLIC -----------

    @GetMapping("/public/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponsePageDTO> getAllCategories(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortDir) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortDir));
    }

}
