package ru.permintel.saga

import javax.servlet.http.HttpServletResponse
import org.apache.commons.lang.StringUtils
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SagaFileController {
    private Closure withInstance(Closure cl) {
        return {
            SagaFile file = SagaFile.get(params.id);
            if (file) {
                return cl(file);
            } else {
                redirect(status: HttpServletResponse.SC_NOT_FOUND, text: message(code: 'saga.domain.file.not.found'));
            }
        };
    }

    def download = withInstance {SagaFile file ->
        if(ConfigurationHolder.config.g2b.cache.dir){
            File fs = new File(ConfigurationHolder.config.g2b.cache.dir as String, file.id.toString());
            if(!fs.exists()){
                fs.createNewFile();
                OutputStream os = new FileOutputStream(fs);
                try{
                    os.write(file.content);
                }finally{
                    os.close();
                }
            }
            sendFile(
                    fs,
                    [
                            filename:file.name,
                            contentType:file.mimetype,
                            dispositionType:file.isImage()?'inline':'attachment'
                    ]
            );
        }else{
            sendDirectly(request, response, file);
        }
        return null;
    }

    private def sendDirectly(HttpServletRequest request, HttpServletResponse response, SagaFile file) {
        // File creation date
        long time = file.date.getTime();

        // is file modified?
        boolean modified = true;
        try {
            long since = request.getDateHeader("If-Modified-Since");
            modified = (since < time - 1000);     // 1 second (1000 millisecond) to compensate rounding error
        } catch (IllegalArgumentException e) {
            // Nothing!!!
        }

        // Set common headers
        response.setDateHeader("Date", time);
        response.setDateHeader("Last-Modified", time);
        response.setHeader("Pragma", "none");
        response.addHeader('Expires', 'Fri, 04 Aug 2078 12:00:00 GMT')
        response.setHeader("Etag", "\"sagafile_" + file.id + "\"");
        response.setHeader("Cache-Control", "max-age=604800,public");

        if (modified) {
            // Set status 200 (OK)
            response.setStatus(HttpServletResponse.SC_OK);

            // Set content headers
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType(file.mimetype);
            response.setContentLength((int) file.size);
            response.setHeader(
                    "Content-Disposition",
                    contentDisposition(file, request.getHeader("User-Agent"))
            );

            // write file content
            OutputStream out = response.outputStream;
            out.write(file.content);
            out.flush()
        } else {
            // Send status=304 (Not modified)
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        response.flushBuffer()
    }

    private String contentDisposition(SagaFile file, String userAgent) {
        String contentDisposition;
        if (file.image) {
            contentDisposition = "inline;";
        } else {
            contentDisposition = "attachment;";
        }
        String fileName = URLEncoder.encode(file.name, "utf-8");
        if (
            StringUtils.containsIgnoreCase(userAgent, "firefox") || StringUtils.containsIgnoreCase(userAgent, "opera")
        ) {
            contentDisposition += "filename*=utf-8''" + fileName;
        } else {
            contentDisposition += "filename=\"" + fileName + "\"";
        }
        return contentDisposition;
    }
}
