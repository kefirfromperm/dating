package ru.permintel.saga

class SagaFileContent {
    static mapping = {
        table 'saga_file_content'
        version false
    }

    static belongsTo = [file: SagaFile];

    byte[] content;

    static constraints = {
        content(nullable: false);
    }
}
