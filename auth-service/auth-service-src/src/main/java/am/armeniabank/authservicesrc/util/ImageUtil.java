package am.armeniabank.authservicesrc.util;

import am.armeniabank.authservicesrc.exception.custom.InvalidDocumentException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@UtilityClass
@Slf4j
public class ImageUtil {

    public static String uploadDocument(MultipartFile multipartFile, String path) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new InvalidDocumentException("File is empty");
        }

        String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
        File file = new File(path + fileName);

        if (!documentFilterChain(file)) {
            throw new InvalidDocumentException("Unsupported file format: " + file.getName());
        }

        try {
            multipartFile.transferTo(file);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage(), e);
            throw new InvalidDocumentException("Failed to save file: " + file.getName(), e);
        }
    }

    private static boolean documentFilterChain(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx")
                || name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".txt")
                || name.endsWith(".ppt") || name.endsWith(".pptx");
    }
}
