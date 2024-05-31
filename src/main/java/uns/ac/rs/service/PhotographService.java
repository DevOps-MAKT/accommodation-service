package uns.ac.rs.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotographService {

    private static String uploadDirectory = "src/main/resources/images";

    public String save(InputStream file, String fileName) throws IOException {
        String imageFileName = UUID.randomUUID().toString() + "_" + fileName.replace(' ', '_');
        Files.copy(file, Paths.get(uploadDirectory, imageFileName));
        return imageFileName;
    }
}
