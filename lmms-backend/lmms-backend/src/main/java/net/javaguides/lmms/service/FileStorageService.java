package net.javaguides.lmms.service;


import org.springframework.beans.factory.annotation.Value;
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

    //Nhận giá trị từ file.upload-dir
    public FileStorageService(@Value("${file.upload-dir}") String fileStorageLocation) throws IOException{
        this.fileStorageLocation = Paths.get(fileStorageLocation).toAbsolutePath().normalize();  //chuyển thành đường dẫn ghép với uploads
        if(!Files.exists(this.fileStorageLocation)){
            Files.createDirectories(this.fileStorageLocation); //Tạo thư mục upload nếu chưa có
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(System.currentTimeMillis() + "_" +file.getOriginalFilename()); //Tránh trùng tên file
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}

