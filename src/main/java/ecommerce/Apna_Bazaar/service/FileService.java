package ecommerce.Apna_Bazaar.service;

import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveImage(String url, MultipartFile image);
    Sort pageSort(String sortBy, String sortDir);
}
