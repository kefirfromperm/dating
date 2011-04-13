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

package pl.burningice.plugins.image.engines.scale

import java.awt.Dimension
import magick.ImageInfo
import magick.MagickImage
import pl.burningice.plugins.image.ConfigUtils

/**
 * Class allows to scale image with approximate width and height
 * Result image will not contain exact width and height given by user
 * if there will be image deformation
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
private class ImageMagickApproximateScaleEngine extends ImageMagickScaleEngine {

    protected byte[] scaleImage(byte[] image, Dimension currentSize, Dimension requestedSize) {
        if (requestedSize.width >= currentSize.width
                && requestedSize.height >= currentSize.height){
            return image
        }

        def (scaledWidth, scaledHeight) = calculateSize(currentSize, requestedSize)

        ImageInfo imageSource = new ImageInfo()
        imageSource.setQuality(ConfigUtils.imageMagickQuality)
        imageSource.setCompression(ConfigUtils.imageMagickCompression)
        MagickImage magickImage = new MagickImage(imageSource,  image)
        MagickImage thumbnail = magickImage.scaleImage(scaledWidth, scaledHeight)
        return thumbnail.imageToBlob(imageSource)
    }

    protected def evaluateScale(Dimension currentSize, Dimension requestedSize){
        def scaleX = requestedSize.width / currentSize.width
        def scaleY = requestedSize.height / currentSize.height
        return (scaleX > scaleY ? scaleY : scaleX)
    }

    private List calculateSize(Dimension currentSize, Dimension requestedSize) {
        def scale = evaluateScale(currentSize, requestedSize)
        return [(currentSize.width * scale).toInteger(), (currentSize.height * scale).toInteger()]
    }
}