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

import java.awt.image.BufferedImage
import pl.burningice.plugins.image.file.ImageFile
import java.awt.Dimension
import javax.imageio.ImageIO

/**
 * Class allows to scale image with approximate width and height
 * Result image will not contain exact width and height given by user
 * if there will be image deformation
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
abstract class ImageMagickScaleEngine implements ScaleEngine {

    public BufferedImage execute(ImageFile loadedImage, int width, int height){
        byte[] scaledImage = scaleImage(loadedImage.getAsByteArray(), loadedImage.getSize(), new Dimension(width, height))
        return bytesToBufferedImage(scaledImage)
    }

    abstract protected byte[] scaleImage(byte[] image, Dimension currentSize, Dimension requestedSize)

    protected BufferedImage bytesToBufferedImage(byte[] image){
        return ImageIO.read(new ByteArrayInputStream(image))    
    }
}