package com.greetim

import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile
import pl.burningice.plugins.image.BurningImageService
import pl.burningice.plugins.image.container.SaveCommand
import ru.permintel.saga.SagaFile
import ru.permintel.saga.SagaFileService
import org.apache.commons.io.FileUtils

/**
 * Upload photo, scale, save to the DB and add to profile
 * @author Vitaliy Samolovskih aka Kefir
 */
@Validateable
class UploadPhotoCommand implements SaveCommand {
    private static final Logger log = LogManager.getLogger(UploadPhotoCommand.class);

    // Max photo size is 8Mb
    private static final int MAX_PHOTO_SIZE = 8 * 1024 * 1024;
    String dir = System.getProperty("java.io.tmpdir");

    // Fields
    MultipartFile file;
    String url;
    long version;
    Profile profile;

    // Output
    File localFile;
    String filename = 'photo';
    String contentType = 'image/jpeg';
    SagaFile photoFile;

    // Services
    SagaFileService sagaFileService;
    BurningImageService burningImageService;

    static constraints = {
        file(
                nullable: true,
                validator: {MultipartFile value, UploadPhotoCommand command ->
                    if (value != null && !value.empty) {
                        if (!value.contentType.startsWith('image')) {
                            return ['profile.photo.content.invalid'];
                        }

                        if (value.size > MAX_PHOTO_SIZE) {
                            return ['profile.photo.too.big', MAX_PHOTO_SIZE];
                        }
                    } else if (StringUtils.isBlank(command.url)) {
                        command.errors.reject('profile.photo.empty', 'Select a picture!');
                    }

                    return true;
                }
        );

        url(nullable: true, blank: true, url: true);
        profile(nullable: false);
        version(validator: {long val, UploadPhotoCommand obj -> return obj.profile.version == val});
    }

    /**
     * Execute command. Upload a photo and save it to the DB.
     */
    public void execute() {
        Closure scale = {
            it.scaleApproximate(
                    ConfigurationHolder.config.dating.photo.horizontal.size,
                    ConfigurationHolder.config.dating.photo.vertical.size
            );
        };

        if (file != null && !file.empty) {
            filename = file.originalFilename;
            contentType = file.contentType;

            try {
                burningImageService.doWith(file).execute(this, scale);
            } catch (Exception e) {
                log.error('Can\'t scale image.', e);
                profile.errors.rejectValue('file', 'profile.photo.content.invalid', 'Error!');
            }
        } else {
            downloadFile();
            if (!hasErrors()) {
                try {
                    burningImageService.doWith(localFile.absolutePath, dir).execute(this, scale);
                } catch (Exception e) {
                    log.error('Can\'t scale image.', e);
                    profile.errors.rejectValue('url', 'profile.photo.content.invalid', 'Error!');
                }
            }
            if (localFile != null) {
                localFile.delete();
            }
        }

        if (!hasErrors()) {
            profile.photo = photoFile;
            profile.useGravatar = false;
            profile.save(flush: true);
        }
    }

    @Override
    void execute(byte[] source, String extension) {
        photoFile = sagaFileService.save(
                [name: filename, mimetype: contentType, content: source]
        );
    }

    public void downloadFile() {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet((String) url);
            HttpResponse response = httpClient.execute(get);
            int code = response.getStatusLine().statusCode;
            HttpEntity entity = response.getEntity();
            if (entity) {
                if (code == HttpStatus.SC_OK) {
                    boolean errorFlag = false;

                    if (entity.contentType != null && !entity.contentType.value.startsWith("image")) {
                        this.errors.rejectValue('url', 'profile.photo.content.invalid');
                        errorFlag = true;
                    } else {
                        contentType = entity.contentType.value;
                    }

                    if (entity.contentLength > MAX_PHOTO_SIZE) {
                        this.errors.rejectValue(
                                'url', 'profile.photo.too.big', [MAX_PHOTO_SIZE].toArray(),
                                'Picture is too big!'
                        );
                    }

                    if (!errorFlag) {
                        localFile = new File(dir, UUID.randomUUID().toString() + extension(contentType));
                        OutputStream stream = new BufferedOutputStream(new FileOutputStream(localFile));
                        try {
                            IOUtils.copy(entity.content, stream);
                            stream.flush();
                        } finally {
                            stream.close();
                        }
                    }
                }
                entity.consumeContent();
            }

            if (localFile != null && localFile.size() > MAX_PHOTO_SIZE) {
                this.errors.rejectValue(
                        'url', 'profile.photo.too.big', [MAX_PHOTO_SIZE].toArray(),
                        'Picture is too big!'
                );
            }
        } catch (Exception e) {
            log.error('Can\'t download photo.', e);
            this.errors.rejectValue(
                    'url', 'profile.photo.download.error',
                    'Can\'t download photo.'
            );
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private String extension(String contentType) {
        if (StringUtils.containsIgnoreCase(contentType, 'gif')) {
            return '.gif';
        } else if (StringUtils.containsIgnoreCase(contentType, 'png')) {
            return '.png';
        } else {
            return '.jpeg';
        }
    }
}
