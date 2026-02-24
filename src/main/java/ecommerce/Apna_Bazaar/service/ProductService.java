package ecommerce.Apna_Bazaar.service;

import ecommerce.Apna_Bazaar.payload.request.ProductRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponsePageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long productId);
    ProductResponseDTO getProductById(Long productId);
    ProductResponsePageDTO getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);
    ProductResponsePageDTO getAllProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
    ProductResponseDTO updateImage(Long productId, MultipartFile image);
}
