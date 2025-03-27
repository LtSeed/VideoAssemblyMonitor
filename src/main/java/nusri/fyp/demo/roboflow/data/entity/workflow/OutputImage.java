package nusri.fyp.demo.roboflow.data.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

/**
 * Represents an output image, which can either be in base64 format or have associated metadata.
 * <br> This class is used to handle images that are part of the output from a workflow, where the image data
 * can be either base64 encoded or contain additional video metadata.
 */
@Slf4j
@Data
public class OutputImage {

    /**
     * The type of image data, typically "base64" or another format.
     */
    private String type;

    /**
     * The value of the image, which may either be a base64 encoded string or other image format.
     */
    private String value;

    /**
     * Metadata related to the video, if applicable.
     */
    @JsonProperty("video_metadata")
    private VideoMetadata videoMetadata;

    /**
     * Returns a string representation of the OutputImage, including its type, size of the value, and video metadata.
     *
     * @return String representation of the OutputImage
     */
    @Override
    public String toString() {
        return "OutputImage [type=" + type + ", valueSize=" + value.length() + ", videoMetadata=" + videoMetadata + "]";
    }

    /**
     * Saves the image (if the type is base64) to the specified directory by decoding the base64 string.
     * <br> The image is saved with a timestamp-based filename and a ".png" extension.
     *
     * @param dir The target directory path where the image will be saved.
     */
    public void saveToDir(String dir) {
        if ("base64".equalsIgnoreCase(this.type) && this.value != null) {
            try {
                // 1. Create the directory if it doesn't exist
                Path targetDir = Paths.get(dir);
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                // 2. Generate the output file path with a timestamp-based filename
                Path outputPath = targetDir.resolve(new Date().getTime() + ".png");

                // 3. Decode the base64 value
                byte[] decodedBytes = Base64.getDecoder().decode(this.value);

                // 4. Write the decoded bytes to the file
                Files.write(outputPath, decodedBytes);

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
