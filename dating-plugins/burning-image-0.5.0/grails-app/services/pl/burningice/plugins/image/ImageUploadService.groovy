/*
Copyright (c) 2009 Pawel Gdula <pawel.gdula@burningice.pl>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package pl.burningice.plugins.image

import org.springframework.web.multipart.MultipartFile
import pl.burningice.plugins.image.container.*
import pl.burningice.plugins.image.engines.scale.ScaleType
import pl.burningice.plugins.image.ast.intarface.*;
import pl.burningice.plugins.image.engines.Worker

/**
 * Service for image upload handling
 *
 * @author pawel.gdula@burningice.pl
 */
class ImageUploadService {

    boolean transactional = true

    ResourcePathProvider resourcePathProvider

    ContainerWorkerFactory containerWorkerFactory

    BurningImageService burningImageService

    /**
         * Perfrom save/update of image. Use configuration data to specify how many images
         * should be created and what type of actions should be performed.
         *
         * Exanple configuration
         *
         * CH.config.bi.MyDomain = [
         *     outputDir: '/path/to/outputDir', {nullable = false, blank = false, exists = true}
         *     prefix: '/path/to/outputDir', {nullable = true, blank = false}
         *     images: ['small':[scale:[width:xx, height:yy, type[e:SCALE_ENGINE]]
         *              'medium:[scale:[width:xx, height:yy, type:SCALE_ENGINE],
         *                       watermark:[sign:'/path/to/watermark', offset:[valid offset]]],
         *              'large':[scale:[width:xx, height:yy, type:SCALE_ENGINE],
         *                       watermark:[sign:'/path/to/watermark', offset:[valid offset]]]
         *     ]
         *  ]
         *
         * @param imageContainer Domain object marked by FileImageContainer annotation
         * @param shouldBeSaved Delineate if specifed domain object should be saved (optional)
         * @param actionWraper Closure that allow user to wrap prediefined action by some additional steps (optional)
         * @return FileImageContainer updated image container
         */
    def save(ImageContainer imageContainer) {
        execute(imageContainer, imageContainer.getImage(), null)
    }

    def save(ImageContainer imageContainer, boolean shouldBeSaved) {
        save(imageContainer, shouldBeSaved, null)
    }

    def save(ImageContainer imageContainer, Closure actionWraper) {
        execute(imageContainer, imageContainer.getImage(), actionWraper)
    }

    def save(ImageContainer imageContainer, boolean shouldBeSaved, Closure actionWraper) {
        execute(imageContainer, imageContainer.getImage(), actionWraper)

        if (!shouldBeSaved){
            return imageContainer
        }

        return imageContainer.save(flush:true)
    }

    /**
        * Allows to delete images associated with specified domain object
        *
        * @param imageContainer Domain object marked by FileImageContainer annotation
        * @param shouldBeSaved Delineate if specified domain object should be saved (optional)
        * @return FileImageContainer updated image container
        */
    ImageContainer delete(final ImageContainer imageContainer, shouldBeSaved = false) {
        // get worker that will provide actions for specified container
        ContainerWorker uploadWorker =  containerWorkerFactory.produce(imageContainer)
        // check if there are images - if not leave
        if (!uploadWorker.hasImage()){
            return uploadWorker.container 
        }
        // perform image delete
        uploadWorker.delete()
        // check container should be save - if not leave
        if (!shouldBeSaved){
            return uploadWorker.container
        }
        // perform update
        return uploadWorker.container.save(flush:true)
    }

    /**
         * Execute actions on image
         *
         * @param imageContainer Domain object marked by FileImageContainer annotation
         * @param uploadedImage Image that should be stored
         * @param actionWrapper Closure that allow user to wrap predefined action by some additional steps (optional)
         * @return FileImageContainer updated image container
         */
    private ImageContainer execute(final ImageContainer imageContainer, MultipartFile uploadedImage, Closure actionWrapper) {
        // get worker that will provide actions for specified container
        ContainerWorker uploadWorker =  containerWorkerFactory.produce(imageContainer)
        // check if container is saved        
        if (!uploadWorker.isPersisted()){
            throw new IllegalArgumentException("Container ${uploadWorker} should be persisted")
        }
        //  check if container have provided configuration
        if (!uploadWorker.config){
            throw new IllegalArgumentException("There is no configuration for ${uploadWorker}")
        }
        // if there are images remove them - we will update 
        if (uploadWorker.hasImage()){
            uploadWorker.delete()                        
        }
        // retrieve worker that will perform actions on image
        Worker manipulationWorker = burningImageService.doWith(uploadedImage)
        // do each configured image manipulation
        uploadWorker.config.images.each {subImageName, subImageOperations ->
            manipulationWorker.execute(uploadWorker.getSaveCommand(subImageName), {image ->
                // execute in user specified wrapper
                if (actionWrapper) {
                    actionWrapper(image, subImageName, {
                        executeOnImage(image, subImageOperations)
                    })
                }
                // execute directly
                else {
                    executeOnImage(image, subImageOperations)
                }
             })
        }
        // return image container for other actions
        return uploadWorker.container
    }

    /**
         * Perform specified chain of modification configured by the user
         *
         * @param image Image that is modified
         * @param subImageOperations Configuration of specified modifications
         */
    private def executeOnImage(image, subImageOperations) {
        subImageOperations.each {operationName, params ->
            actionMapping[operationName](image, params)
        }
    }

    /**
         * Performs scaling on image
         *
         * @param image Image on witch scaling should be performed
         * @param params Scaling parameters
         */
    private def scaleImage = {image, params ->
        if (params.type == ScaleType.ACCURATE){
            image.scaleAccurate(params.width, params.height)
            return
        }

        if (params.type == ScaleType.APPROXIMATE){
            image.scaleApproximate(params.width, params.height)
            return
        }

        throw IllegalArgumentException("There is no scale type ${params.type},  check your configuration")
    }

    /**
         * Performs watermarking on image
         *
         * @param image Image on witch watermarking should be performed
         * @param params Scaling parameters
         */
    private def watermarkImage = {image, params ->
        image.watermark(resourcePathProvider.getPath(params.sign), params.offset)
    }

    /**
         * Map configuration key to action in this service
         *
         */
    private def actionMapping = [
        scale:scaleImage,
        watermark:watermarkImage
    ]
}
