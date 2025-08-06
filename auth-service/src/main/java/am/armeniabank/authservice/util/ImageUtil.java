package am.armeniabank.authservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@UtilityClass
@Slf4j
public class ImageUtil {

    public static String uploadDocument(MultipartFile multipartFile, String path) {
        String fileName;
        try {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(path + fileName);
                if (documentFilterChain(file)) {
                    multipartFile.transferTo(file);
                    return fileName;
                }
                throw new IOException("Wrong file format");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static boolean documentFilterChain(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx")
                || name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".txt")
                || name.endsWith(".ppt") || name.endsWith(".pptx");
    }
}
