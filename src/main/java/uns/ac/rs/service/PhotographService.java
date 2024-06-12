package uns.ac.rs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uns.ac.rs.controller.AccommodationController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotographService {

    private static String uploadDirectory = "src/main/resources/images";
    private static final Logger logger = LoggerFactory.getLogger(PhotographService.class);

    public String save(InputStream file, String fileName) throws IOException {
        try {
            if (!Files.exists(Paths.get(uploadDirectory))) {
                Files.createDirectories(Paths.get(uploadDirectory));
            }
            String imageFileName = UUID.randomUUID().toString() + "_" + fileName.replace(' ', '_');
            Files.copy(file, Paths.get(uploadDirectory, imageFileName));
            return imageFileName;
        } catch (Exception e) {
            logger.error("Error while saving the image: {}", e.getMessage());
        }
        return "";
    }
}
