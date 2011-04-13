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
import magick.MagickImage
import magick.ImageInfo
import java.awt.image.BufferedImage
import java.awt.Rectangle
import pl.burningice.plugins.image.ConfigUtils

/**
 * Class allows to scale image with accurate width and height.
 * Result image will  contain exact width and height gave by user,
 * if original size of image not fit to user width and height, image will be scaled
 * to shortest side and cropped on center
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
private class ImageMagickAccurateScaleEngine extends ImageMagickApproximateScaleEngine {

    /**
     * Specify image size correction for scaling
     *
     * Sometimes during scaling there are situations
     * when one of the sides don't match requested size (is lover by one px)
     * Can be fixed by adding this 1px to image size before scale calculations.
     * This is not important during approximate scaling, but required during accurate scaling
     * to match exactly requested image size.
     */
    private static final int SIZE_CORRECTION = 1

    protected byte[] scaleImage(byte[] image, Dimension currentSize, Dimension requestedSize) {
        byte[] rawScaledImage = super.scaleImage(image, currentSize, requestedSize)
        BufferedImage scaledImage = bytesToBufferedImage(rawScaledImage)

        if (scaledImage.width ==  requestedSize.width
            && scaledImage.height == requestedSize.height ){
            return rawScaledImage
        }

        int offsetX = Math.round((scaledImage.width - requestedSize.width) / 2)
        int offsetY = Math.round((scaledImage.height - requestedSize.height) / 2)

        Rectangle cropData = new Rectangle(offsetX, offsetY, (int)requestedSize.width, (int)requestedSize.height)

        ImageInfo imageSource = new ImageInfo()
        imageSource.setQuality(ConfigUtils.imageMagickQuality)
        imageSource.setCompression(ConfigUtils.imageMagickCompression)
        MagickImage magickImage = new MagickImage(imageSource,  rawScaledImage)
        MagickImage croppedImage = magickImage.cropImage(cropData)
        return croppedImage.imageToBlob(imageSource)
    }

    protected def evaluateScale(Dimension currentSize, Dimension requestedSize){
        def scaleX = (requestedSize.width + SIZE_CORRECTION) / currentSize.width
        def scaleY = (requestedSize.height + SIZE_CORRECTION) / currentSize.height
        return (scaleX < scaleY ? scaleY : scaleX)
    }
}