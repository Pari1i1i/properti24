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

    /**
     * Saves a base64 / data URL image string (e.g. from camera capture) and returns the saved filename.
     */
    public static String saveBase64Image(String base64Data, String prefix) {
        if (base64Data == null || base64Data.isBlank()) {
            return null;
        }
        try {
            String clean = base64Data.trim();
            String extension = ".jpg";
            if (clean.contains(",")) {
                String header = clean.substring(0, clean.indexOf(","));
                if (header.contains("image/png")) {
                    extension = ".png";
                } else if (header.contains("image/webp")) {
                    extension = ".webp";
                }
                clean = clean.substring(clean.indexOf(",") + 1);
            }
            byte[] decoded = java.util.Base64.getDecoder().decode(clean.trim());

            String userDir = System.getProperty("user.dir");
            Path srcImagesDir = Paths.get(userDir, "src", "main", "resources", "META-INF", "resources", "images");
            Path targetImagesDir = Paths.get(userDir, "target", "classes", "META-INF", "resources", "images");

            Files.createDirectories(srcImagesDir);
            Files.createDirectories(targetImagesDir);

            String safePrefix = (prefix != null && !prefix.isBlank())
                    ? prefix.replaceAll("[^a-zA-Z0-9._\\-]", "_")
                    : "camera";
            String unique = System.currentTimeMillis() + "_" + safePrefix + extension;

            Path destSrc = srcImagesDir.resolve(unique);
            Files.write(destSrc, decoded, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Also copy to target/classes for immediate serving
            Path destTarget = targetImagesDir.resolve(unique);
            try {
                Files.copy(destSrc, destTarget, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}

            return unique;
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan foto kamera: " + e.getMessage(), e);
        }
    }
}

