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
package pl.burningice.plugins.image.engines.text

import javax.imageio.ImageIO
import magick.ImageInfo
import magick.MagickImage
import java.awt.image.BufferedImage
import magick.DrawInfo
import magick.PixelPacket
import java.awt.Color
import java.awt.Font
import pl.burningice.plugins.image.file.ImageFile
import pl.burningice.plugins.image.ConfigUtils

/**
 * Engine for typing text on image
 *
 * @author pawel.gdula@burningice.pl
 */
final class ImageMagickTextEngine extends DefaultTextEngine {

    private MagickImage magickImage

    private ImageInfo imageSource

    private PixelPacket imageMagickColor

    private static final int JMAGICK_COLOR_MAX_VALUE = 0xffff

    private static final int CLIENT_COLOR_MAX_VALUE = 255

    public ImageMagickTextEngine(Color color, Font font, ImageFile loadedImage){
        super(color, font, loadedImage)
    }

    void write(String text, int deltaX, int deltaY) {
        DrawInfo aInfo = new DrawInfo(imageSource);

        if (imageMagickColor) {
            aInfo.setFill(imageMagickColor)
        }

        if (font){
            aInfo.setPointsize(font.size)
            aInfo.setFont(font.name)   
        }

        aInfo.setGeometry("+${deltaX}+${deltaY}")
        aInfo.setText(text)
        aInfo.setTextAntialias(true)
        magickImage.annotateImage(aInfo) 
    }

    BufferedImage getResult(){
        return ImageIO.read(new ByteArrayInputStream(magickImage.imageToBlob(imageSource)))
    }

    protected void init() {
        imageSource = new ImageInfo()
        imageSource.setQuality(ConfigUtils.imageMagickQuality)
        imageSource.setCompression(ConfigUtils.imageMagickCompression)

        magickImage = new MagickImage(imageSource, loadedImage.getAsByteArray())

        if (color) {
            imageMagickColor = new PixelPacket(recalculateColorRange(color.red),
                                                    recalculateColorRange(color.green),
                                                    recalculateColorRange(color.blue),
                                                    0)
        }
    }

    private int recalculateColorRange(int currentRange){
        return (currentRange/CLIENT_COLOR_MAX_VALUE) * JMAGICK_COLOR_MAX_VALUE
    }
}