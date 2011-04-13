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
package pl.burningice.plugins.image.container

import pl.burningice.plugins.image.ast.intarface.ImageContainer
import pl.burningice.plugins.image.ConfigUtils

/**
 * Provide helper methods for working with ImageContainers
 *
 * @author pawel.gdula@burningice.pl
 */
class ContainerUtils {

    private static final def NAME_ELEMENTS_DELIMITER = '-'

    /**
     * Retrieve file extension from name of file
     *
     * @param imageName File name
     * @return File extension
     */
    static def getImageExtension(String imageName){
        imageName.split(/\./)[-1].toLowerCase()
    }

    /**
     * Returns configuration for specified container
     *
     * @param imageContainer Image container for witch configuration is searched
     * @return Configuration data for specified container
     */
    static def getConfig(ImageContainer imageContainer){
        ConfigUtils.getContainerConfig(getImageContainerName(imageContainer))
    }

    /**
     * Returns class name for specified container
     *
     * @param imageContainer Image container for witch configuration is searched
     * @return Name of the class
     */
    static def getImageContainerName(imageContainer){
        // search for name, can be sufixed by $$_javassist (hibernate proxy)
        return imageContainer.class.simpleName.split('_')[0]
    }

    /**
     * Returns full name (with extension) for image with specified size
     *
     * @param sizeName Image size that we looking for
     * @param imageContainer Image container for witch image is searched
     * @param config Configuration of specified imageContainer
     * @return Name of the file
     */
    static def getFullName(sizeName, ImageContainer imageContainer, config){
        "${getName(sizeName, imageContainer, config)}.${imageContainer.imageExtension}"
    }

    /**
     * Returns name (without extension) for image with specified size
     *
     * @param sizeName Image size that we looking for
     * @param imageContainer Image container for witch image is searched
     * @param config Configuration of specified imageContainer
     * @return Name of the file
     */
    static def getName(sizeName, ImageContainer imageContainer, config){
        def nameElements = []

        if (config.prefix){
            nameElements << config.prefix
        }

        nameElements << imageContainer.ident()
        nameElements << sizeName
        nameElements.join(NAME_ELEMENTS_DELIMITER)
    }
}

