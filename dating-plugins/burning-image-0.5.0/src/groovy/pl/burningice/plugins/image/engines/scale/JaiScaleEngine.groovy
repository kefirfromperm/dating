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

/**
 * Abstract class for all scale engines
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
abstract class JaiScaleEngine implements ScaleEngine {

    /**
     * Execute image scaling
     *
     * @param ImageFile loadedImage Loaded image
     * @param int width Requested width
     * @param int height Requested height
     * @return BufferedImage
     */
    public BufferedImage execute(ImageFile loadedImage, int width, int height){
        def scaledImage = scaleImage(loadedImage.getAsJaiStream(), width, height)
        scaledImage.getAsBufferedImage()
    }

    /**
     * Start scaling transformation
     *
     * @param RenderedOp image Image to scale
     * @param int width Requested width
     * @param int height Requested height
     * @return RenderedOp
     */
    abstract protected def scaleImage(image, width, height)
}