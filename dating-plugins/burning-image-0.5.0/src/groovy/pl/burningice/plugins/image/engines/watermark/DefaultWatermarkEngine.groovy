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

package pl.burningice.plugins.image.engines.watermark

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import pl.burningice.plugins.image.file.ImageFile
import java.awt.Point

/**
 * Base class for any watermark engine used by BI
 *
 * @author pawel.gdula@burningice.pl
 */
abstract class DefaultWatermarkEngine implements WatermarkEngine {

    public BufferedImage execute(File watermarkFile, ImageFile loadedImage, Map position, float alpha){
        return doWatermark(watermarkFile,
                            loadedImage,
                            position,
                            alpha,
                            transformPosition(ImageIO.read(watermarkFile), loadedImage.getSize(), position))
    }

    abstract protected BufferedImage doWatermark(File watermarkFile, ImageFile loadedImage, Map position, float alpha, Point offset)

    protected Point transformPosition(watermark, fileToMark, position){
        def left, top

        if (position['left'] != null) {
            left = position['left']
        }

        if (position['top'] != null) {
            top = position['top']
        }

        if (position['right'] != null) {
            left = fileToMark.width - position['right'] - watermark.width
        }

        if (position['bottom'] != null) {
            top = fileToMark.height - position['bottom'] - watermark.height
        }

        if (!left) {
            left = (fileToMark.width - watermark.width)/2
        }

        if (!top) {
            top = (fileToMark.height - watermark.height)/2
        }

        return new Point((int)left, (int)top)
    }
}