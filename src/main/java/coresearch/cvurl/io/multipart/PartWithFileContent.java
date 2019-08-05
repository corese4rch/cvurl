package coresearch.cvurl.io.multipart;

/**
 * Represent part with file content of multipart data.
 */
public class PartWithFileContent extends Part<PartWithFileContent> {
    private String fileName;

    PartWithFileContent(String fileName, byte[] content) {
        super(content);
        this.fileName = fileName;
    }

    /**
     * Returns file name.
     *
     * @return file name.
     */
    public String getFileName() {
        return fileName;
    }
}
