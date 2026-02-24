package ecommerce.Apna_Bazaar.controller;

import ecommerce.Apna_Bazaar.payload.request.ProductRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponsePageDTO;
import ecommerce.Apna_Bazaar.service.ProductService;
import ecommerce.Apna_Bazaar.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ----------- ADMIN -----------

    @PostMapping("/admin/products")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO response =
                productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.ok(
                productService.updateProduct(id, productRequestDTO));
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PutMapping("/admin/products/{id}/image")
    public ResponseEntity<ProductResponseDTO> updateImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(
                productService.updateImage(id, image));
    }


    // ----------- PUBLIC -----------

    @GetMapping("/public/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponsePageDTO> getAllProducts(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortDir) {
        return ResponseEntity.ok(
                productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/public/products/category/{categoryId}")
    public ResponseEntity<ProductResponsePageDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) int pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortDir) {
        return ResponseEntity.ok(
                productService.getAllProductsByCategoryId(
                        categoryId, pageNumber, pageSize, sortBy, sortDir));
    }
}
