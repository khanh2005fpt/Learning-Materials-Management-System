package net.javaguides.lmms.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageService fileStorageService;
    private final BookRepository bookRepository;

    //Upload file và trả về đường dẫn
    public String uploadFile(MultipartFile file) throws IOException {
        return fileStorageService.storeFile(file);
    }

    //Xóa file + trong database

}
