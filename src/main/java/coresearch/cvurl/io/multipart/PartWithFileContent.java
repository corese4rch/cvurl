package coresearch.cvurl.io.multipart;

import java.nio.file.Path;

/**
 * Represent part with file content of multipart data.
 */
public class PartWithFileContent extends Part<PartWithFileContent> {
    private Path filePath;

    PartWithFileContent(Path filePath, byte[] content) {
        super(content);
        this.filePath = filePath;
    }

    /**
     * Returns file path.
     *
     * @return file path.
     */
    public Path getFilePath() {
        return filePath;
    }
}
