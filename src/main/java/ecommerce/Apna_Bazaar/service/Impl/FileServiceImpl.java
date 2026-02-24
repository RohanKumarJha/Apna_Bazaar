package ecommerce.Apna_Bazaar.service.Impl;

import ecommerce.Apna_Bazaar.exception.BadRequestException;
import ecommerce.Apna_Bazaar.service.FileService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    @Transactional
    public String saveImage(String url, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image is empty");
        }
        String originalName = image.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new BadRequestException("Invalid file name");
        }
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().concat(extension);
        File dir = new File(url);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String filePath = url.concat(File.separator.concat(fileName));
        try {
            Files.copy(image.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return filePath;
    }

    @Override
    @Transactional
    public Sort pageSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }
}
