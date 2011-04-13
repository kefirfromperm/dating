package ru.permintel.saga

import org.apache.commons.io.FilenameUtils

/** File persisted in DB  */
class SagaFile {
    static mapping = {
        table 'saga_file';
        version false;
        cache usage:'read-only', include:'non-lazy';
    }
    public static final String DEFAULT_MIME_TYPE = 'application/octet-stream'

    // User friendly name of file, for example "Big report"
    // For generate file links
    String linkAlias;

    // name of file
    String name;
    String mimetype = DEFAULT_MIME_TYPE;
    Date date = new Date();  // upload date
    long size;
    SagaFileContent fileContent;

    static constraints = {
        linkAlias(nullable: true, blank: true);
        name(nullable: false, blank: false);
        mimetype(nullable: false, blank: false);
        date(nullable: false);
        fileContent(nullable:false);
    }

    static transients = ['content', 'image', 'alias', 'extension'];

    public byte[] getContent() {
        return fileContent?.content;
    }

    public void setContent(byte[] content) {
        this.setFileContent(new SagaFileContent(content: content));
    }

    public boolean isImage() {
        return mimetype.startsWith("image")
    }

    public String getAlias() {
        return linkAlias ?: name;
    }

    public String getExtension() {
        return FilenameUtils.getExtension(name);
    }
}
