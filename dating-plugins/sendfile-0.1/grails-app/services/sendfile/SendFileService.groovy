package sendfile;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import org.apache.commons.io.IOUtils
import java.util.regex.Pattern
import java.util.regex.Matcher
import javax.servlet.ServletContext
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SendFileService {
    private static final int BUFFER_SIZE = 4096;

    static transactional = false;

    /**
     * Send file
     */
    def sendFile(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, File file, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, File file, Map headers=[:])');

        // File creation date
        long time = file.lastModified();

        // is file modified?
        boolean modified = true;
        try {
            long since = request.getDateHeader("If-Modified-Since");
            modified = (since < time - 1000);     // 1 second (1000 millisecond) to compensate rounding error
        } catch (IllegalArgumentException e) {
            // Nothing!!!
        }

        // File path
        def path = file.absolutePath;

        // Set common headers
        response.setDateHeader("Date", time);
        response.setDateHeader("Last-Modified", time);
        response.setHeader("Pragma", "none");
        response.addHeader('Expires', 'Fri, 04 Aug 2078 12:00:00 GMT')
        response.setHeader("Etag", "\"gf_" + path.encodeAsMD5() + "\"");
        response.setHeader("Cache-Control", "max-age=604800,public");

        if (modified) {
            // Set status 200 (OK)
            response.setStatus(HttpServletResponse.SC_OK);

            // Set content headers
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType(mimeType(servletContext, file, headers));
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", contentDisposition(request, file, headers));

            if (isSupportNginx()) {
                log.debug('Use nginx X-Accel-Redirect');
                response.setHeader("X-Accel-Redirect", path);
            } else if (isSupportApache()) {
                log.debug('Use Apache X-Senfile');
                response.setHeader("X-Sendfile", path);
            } else if (isSupportTomcat()) {
                log.debug('Use Tomcat sendfile');
                request.setAttribute("org.apache.tomcat.sendfile.filename", path);
                request.setAttribute("org.apache.tomcat.sendfile.start", (long) 0);
                request.setAttribute("org.apache.tomcat.sendfile.end", file.length());
            } else {
                log.debug('Send file directly to response output stream.');
                sendDirectly(request, response, file);
            }
        } else {
            // Send status=304 (Not modified)
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        response.flushBuffer();
    }

    /**
     * Generate Content-Disposition header
     */
    private String contentDisposition(HttpServletRequest request, File file, Map headers) {
        StringBuilder builder = new StringBuilder();
        builder.append(headers['dispositionType'] ?: 'attachment');
        builder.append('; filename');

        // Filename
        String name = URLEncoder.encode(headers['filename'] as String ?: file.getName());
        String userAgent = request.getHeader("User-Agent");
        if (
            StringUtils.containsIgnoreCase(userAgent, "firefox") || StringUtils.containsIgnoreCase(userAgent, "opera")
        ) {
            builder.append("*=utf-8''");
            builder.append(name);
        } else {
            builder.append("=\"");
            builder.append(name);
            builder.append("\"");
        }
        return builder.toString();
    }

    /**
     * Generate mimetype for file
     */
    private String mimeType(ServletContext servletContext, File file, Map headers) {
        String mimetype = headers['contentType'];
        if (mimetype == null) {mimetype = servletContext.getMimeType(file.name.toLowerCase());}
        if (mimetype == null) {mimetype = headers['defaultContentType'];}
        if (mimetype == null) {mimetype = 'application/octet-stream';}
        return mimetype;
    }

    /**
     * Send file directly
     */
    private sendDirectly(HttpServletRequest request, HttpServletResponse response, File file) {
        List<Integer> range = parseRange(request);

        // Offset
        long skip = 0;
        if (range.size() >= 1) {
            skip = range[0];
        }

        // max byte index
        int size = file.length();
        int max = size;
        if (range.size() >= 2) {
            max = Math.min(max, range[1]);
        }

        // Write "Content-Range" header
        StringBuilder rangeHeader = new StringBuilder();
        rangeHeader.append('bytes');
        if (!range.isEmpty()) {
            rangeHeader.append(' ');
            rangeHeader.append(skip);
            rangeHeader.append('-');
            rangeHeader.append(max);
            rangeHeader.append('/');
            rangeHeader.append(size);
        }

        response.setHeader('Content-Range', rangeHeader.toString());

        // Write content
        InputStream stream = new FileInputStream(file);
        try {
            int offset = 0;
            int len = 0;

            while (offset < skip && len >= 0) {
                len = stream.skip(skip - offset);
                offset += len;
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            while (offset < max && len >= 0) {
                len = stream.read(buffer, 0, Math.min(buffer.length, max - offset));
                if (len > 0) {
                    response.outputStream.write(buffer, 0, len);
                }
            }
        } finally {
            stream.close();
        }
    }

    // Send file by path

    def sendFile(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, String path, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, String path, Map headers=[:])');

        sendFile(servletContext, request, response, new File(path), headers);
    }

    // Send byte content

    def sendFile(HttpServletRequest request, HttpServletResponse response, byte[] content, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, byte[] content, Map headers=[:])');

        IOUtils.write(content, response.outputStream);
        response.flushBuffer();
    }

    // Send content from InputStream

    def sendFile(HttpServletRequest request, HttpServletResponse response, InputStream is, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, InputStream is, Map headers=[:])');

        IOUtils.copy(is, response.outputStream);
        response.flushBuffer();
    }

    def sendFile(HttpServletRequest request, HttpServletResponse response, ByteBuffer content, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, ByteBuffer content, Map headers=[:])');

        throw new UnsupportedOperationException("Method sendFile for ByteBuffer not supported while.");
    }

    def sendFile(HttpServletRequest request, HttpServletResponse response, ReadableByteChannel channel, Map headers = [:]) {
        log.trace('sendFile(HttpServletRequest request, HttpServletResponse response, ReadableByteChannel channel, Map headers=[:])');
        throw new UnsupportedOperationException("Method sendFile for ReadableByteChannel not supported while.");
    }

    boolean isSupportNginx() {
        return ConfigurationHolder.config.grails.plugins.sendfile.nginx;
    }

    boolean isSupportApache() {
        return ConfigurationHolder.config.grails.plugins.sendfile.apache;
    }

    boolean isSupportTomcat() {
        return ConfigurationHolder.config.grails.plugins.sendfile.tomcat;
    }

    /**
     * Parse "Range" header of HTTP-request
     */
    List<Integer> parseRange(HttpServletRequest request) {
        List<Integer> list = [];
        String range = request.getHeader("Range");
        if (range) {
            Pattern pattern = Pattern.compile(/\d+/);
            Matcher matcher = pattern.matcher(range);
            while (matcher.find()) {
                list.add(Integer.decode(matcher.group()));
            }
        }
        return list;
    }
}
