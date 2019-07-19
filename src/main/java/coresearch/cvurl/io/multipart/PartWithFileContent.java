package coresearch.cvurl.io.multipart;

import java.nio.file.Path;

public class PartWithFileContent extends Part<PartWithFileContent> {
    private Path filePath;

    PartWithFileContent(Path filePath, byte[] content) {
        super(content);
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }
}
