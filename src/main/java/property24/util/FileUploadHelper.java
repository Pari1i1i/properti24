package property24.util;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.*;

@Component
public class FileUploadHelper {

    /**
     * Saves an uploaded file into static images folder and returns the filename.
     */
    public static String saveImage(MemoryBuffer buffer, String originalFileName) {
        if (buffer == null || originalFileName == null || originalFileName.isBlank()) {
            return null;
        }
        try {
            String userDir = System.getProperty("user.dir");
            Path srcImagesDir = Paths.get(userDir, "src", "main", "resources", "META-INF", "resources", "images");
            Path targetImagesDir = Paths.get(userDir, "target", "classes", "META-INF", "resources", "images");

            Files.createDirectories(srcImagesDir);
            Files.createDirectories(targetImagesDir);

            String sanitized = originalFileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
            String unique = System.currentTimeMillis() + "_" + sanitized;

            Path destSrc = srcImagesDir.resolve(unique);
            try (InputStream in = buffer.getInputStream()) {
                Files.copy(in, destSrc, StandardCopyOption.REPLACE_EXISTING);
            }

            // Also copy to target/classes so Vaadin serves it instantly during runtime
            Path destTarget = targetImagesDir.resolve(unique);
            try {
                Files.copy(destSrc, destTarget, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}

            return unique;
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan foto: " + e.getMessage(), e);
        }
    }
}

