package ru.permintel.saga

import org.springframework.web.multipart.MultipartFile

class SagaFileService {
    static transactional = true

    SagaFile save(Map args){
        SagaFile file = new SagaFile(
                name: args.name,
                mimetype: args.mimetype?:SagaFile.DEFAULT_MIME_TYPE,
                size: args.size?:args.content.length,
                content: args.content,
                linkAlias: args.alias
        );

        SagaFile.withTransaction {
            file.save();
        }

        return file;
    }

    SagaFile save(MultipartFile mpf, String alias = null) {
        return save(
                [name:mpf.originalFilename, mimetype:mpf.contentType, size:mpf.size, content: mpf.bytes, alias:alias]
        );
    }

    SagaFile save(File f, String alias = null) {
        int size = f.size()
        byte[] content = new byte[size];

        InputStream stream = new BufferedInputStream(new FileInputStream(f));
        try {
            int offset = 0;
            int len;
            while (offset < size && ((len = stream.read(content, offset, size - offset)) > 0)) {
                offset += len;
            }
        } finally {
            stream.close();
        }

        return save([name:f.name, size:size, content: content, alias:alias]);
    }
}
