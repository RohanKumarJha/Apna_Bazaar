package ecommerce.Apna_Bazaar.service.Impl;

import ecommerce.Apna_Bazaar.exception.ResourceAlreadyExistException;
import ecommerce.Apna_Bazaar.exception.ResourceNotExistException;
import ecommerce.Apna_Bazaar.model.Category;
import ecommerce.Apna_Bazaar.model.Product;
import ecommerce.Apna_Bazaar.payload.request.ProductRequestDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponseDTO;
import ecommerce.Apna_Bazaar.payload.response.ProductResponsePageDTO;
import ecommerce.Apna_Bazaar.repository.CategoryRepository;
import ecommerce.Apna_Bazaar.repository.ProductRepository;
import ecommerce.Apna_Bazaar.service.FileService;
import ecommerce.Apna_Bazaar.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final String CACHE_NAME = "products";
    private final FileService fileService;

    @Value("${app.upload.dir}")
    private String url;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        if (productRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistException(
                    "Product already exists with name " + dto.getName());
        }
        Category category = getCategoryIfExists(dto.getCategoryId());
        Product product = productRequestDTOToProduct(dto, category);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO dto) {
        Product product = getProductIfExists(productId);
        if (!product.getName().equals(dto.getName())
                && productRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistException(
                    "Product already exists with name " + dto.getName());
        }
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setImage(dto.getImage());
        product.setPrice(dto.getPrice());
        product.setCategory(getCategoryIfExists(dto.getCategoryId()));
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.delete(getProductIfExists(productId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_NAME, key = "#productId")
    public ProductResponseDTO getProductById(Long productId) {
        Product product = getProductIfExists(productId);
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    public ProductResponsePageDTO getAllProducts(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable =
                PageRequest.of(pageNumber, pageSize, fileService.pageSort(sortBy, sortDir));
        Page<Product> page = productRepository.findAll(pageable);
        List<Product> products = page.getContent();
        List<ProductResponseDTO> productResponseDTO = new ArrayList<>();
        for (Product product : products) {
            productResponseDTO.add(
                    modelMapper.map(product, ProductResponseDTO.class)
            );
        }
        return productResponseDTO_To_ProductResponsePageDTO(productResponseDTO, page);
    }

    @Override
    public ProductResponsePageDTO getAllProductsByCategoryId(
            Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, fileService.pageSort(sortBy, sortDir));
        Page<Product> page = productRepository.findByCategoryId(categoryId, pageable);
        List<ProductResponseDTO> productResponseDTO = new ArrayList<>();
        for (Product product : page.getContent()) {
            productResponseDTO.add(modelMapper.map(product, ProductResponseDTO.class));
        }
        return productResponseDTO_To_ProductResponsePageDTO(productResponseDTO, page);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateImage(Long productId, MultipartFile image) {
        Product product = getProductIfExists(productId);
        String fileName = fileService.saveImage(url, image);
        product.setImage(fileName);
        return modelMapper.map(productRepository.save(product), ProductResponseDTO.class);
    }

    //    ---------------------------------- Helper methods -------------------------------------------
    private Category getCategoryIfExists(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotExistException("Category not exist with id " + id));
    }

    private Product productRequestDTOToProduct(ProductRequestDTO dto, Category category) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .image(dto.getImage())
                .price(dto.getPrice())
                .category(category)
                .build();
    }

    private Product getProductIfExists(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new ResourceNotExistException("Product not exist with id " + id));
    }

    private ProductResponsePageDTO productResponseDTO_To_ProductResponsePageDTO(
            List<ProductResponseDTO> productResponseDTO,Page<Product> page) {
        ProductResponsePageDTO productResponsePageDTO = new ProductResponsePageDTO();
        productResponsePageDTO.setProducts(productResponseDTO);
        productResponsePageDTO.setPageNumber(page.getNumber());
        productResponsePageDTO.setPageSize(page.getSize());
        productResponsePageDTO.setTotalPages(page.getTotalPages());
        productResponsePageDTO.setTotalElements(page.getTotalElements());
        productResponsePageDTO.setLast(page.isLast());
        return productResponsePageDTO;
    }

}