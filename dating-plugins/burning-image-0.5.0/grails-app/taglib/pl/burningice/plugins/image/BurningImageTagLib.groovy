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

import pl.burningice.plugins.image.container.ContainerUtils
import pl.burningice.plugins.image.ast.intarface.FileImageContainer
import pl.burningice.plugins.image.container.ContainerWorker
import pl.burningice.plugins.image.container.ContainerWorkerFactory
import pl.burningice.plugins.image.ast.intarface.DBImageContainer
import pl.burningice.plugins.image.ast.Image

/**
  * Taglib for usage with annotated image container
  *
  * @author pawel.gdula@burningice.pl
  */
class BurningImageTagLib {

    static namespace = 'bi'

    ContainerWorkerFactory containerWorkerFactory   

    /**
     * Allows to check if specified image container has saved image
     *
     * @param bean Image container that hold information about image
     */
    def hasImage =  { attrs, body ->
        if (!attrs.bean
            || !containerWorkerFactory.produce(attrs.bean).hasImage()){
            return
        }

        out << body()
    }

    /**
     * Display html img tag with path to stored image
     *
     * @param size Size of the image that should be displayed
     * @param bean Image container that hold information about image
     */
    def img =  { attrs, body ->
        def path = resource(attrs, body)

        if (!path){
            return null
        }

        attrs.remove('size')
        attrs.remove('bean')

        def htmlAttributes = attrs.collect {it.key + '="' + it.value + '"'}
        out << "<img src=\"${path}\" ${htmlAttributes.join(' ')}/>"
    }

    /**
     * Create link to image stored on the server
     *
     * @param size Size of the image that should be displayed
     * @param bean Image container that hold information about image
     */
    def resource = {attrs, body ->
        if (!attrs.size || !attrs.bean){
            throw new IllegalArgumentException("Atrribute bean and size can't be empty/null")
        }

        if (!attrs.bean.ident()){
            return null
        }

        out << getResourceData(attrs.size, attrs.bean)
    }

    /**
     * Retrieve information about file name and storage directory on base
     * of image size name and image container object
     *
     * @param size Size of the image that should be displayed
     * @param bean Image container that hold information about image
     */
    private def getResourceData(size, FileImageContainer imageContainer){
        def config = ContainerUtils.getConfig(imageContainer)

        if (!config){
            throw new IllegalArgumentException("There is no config for ${imageContainer.class.name}")
        }

        return g.resource(dir:getOutputDir(config.outputDir), file:ContainerUtils.getFullName(size, imageContainer, config))
    }

     /**
     * Retrieve information about file name and storage directory on base
     * of image size name and image container object
     *
     * @param size Size of the image that should be displayed
     * @param bean Image container that hold information about image
     */
    private def getResourceData(size, DBImageContainer imageContainer){
        Image image = imageContainer.biImage[size]

        if (!image){
            throw new IllegalArgumentException("There is no image with size ${size} saved for container ${imageContainer.class.name}")          
        }

        g.createLink(controller:'dbContainerImage', action:'index', params:[imageId:image.ident(), size:size, type:image.type])
    }

    /**
        * Returns directory where image is stored. Overloaded to provide dispatching between String and Map outputDir
        *
        * @param uploadDir Path to upload dir
        * @return Absolute path to resources
        */
    private def getOutputDir(String outputDir){
        return outputDir
    }

    /**
        * Returns directory where image is stored. Overloaded to provide dispatching between String and Map outputDir
        * Parameter uploadDir should contain two keys:
        * path - absolute path to directory where image should saved
        * alias - alias for the absolute path
        *
        * @param uploadDir Map with upload dir configuration.
        * @return Absolute path to resources
        */
    private def getOutputDir(Map outputDir){
        outputDir.alias
    }
}
