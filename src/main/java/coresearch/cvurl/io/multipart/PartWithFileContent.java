package coresearch.cvurl.io.multipart;

/**
 * Represents a part of the content of multipart data.
 *
 * @since 1.0
 */
public class PartWithFileContent extends Part<PartWithFileContent> {
    private String fileName;

    PartWithFileContent(String fileName, byte[] content) {
        super(content);
        this.fileName = fileName;
    }

    /**
     * Returns the name of the file.
     */
    public String getFileName() {
        return fileName;
    }
}
