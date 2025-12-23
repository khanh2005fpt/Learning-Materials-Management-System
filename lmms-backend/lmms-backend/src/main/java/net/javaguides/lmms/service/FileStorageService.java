package net.javaguides.lmms.service;


import net.javaguides.lmms.entity.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation; //Đường dẫn thư mục file

    //Check trùng file gốc
    private boolean isDuplicateOriginalName(String originalFilename) throws IOException {
        try (var paths = Files.list(fileStorageLocation)) {
            return paths.anyMatch(path ->
                    path.getFileName().toString().endsWith("_" + originalFilename)
            );
        }
    }

    //Nhận giá trị từ file.upload-dir
    public FileStorageService(@Value("${file.upload-dir}") String fileStorageLocation) throws IOException{
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();  //chuyển thành đường dẫn ghép với uploads
        if(!Files.exists(this.fileStorageLocation)){
            Files.createDirectories(this.fileStorageLocation); //Tạo thư mục upload nếu chưa có
        }
    }

    public String storeFile(MultipartFile file) throws IOException {

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Chỉ cho phép upload file PDF");
        }

        // Check trùng tên file gốc
        if (isDuplicateOriginalName(originalFilename)) {
            throw new RuntimeException("File PDF có tên '" + originalFilename + "' đã tồn tại");
        }

        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation);

        return fileName;
    }


    public String deleteFile(String fileName) throws IOException {
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.deleteIfExists(targetLocation);
        return fileName;
    }


}

