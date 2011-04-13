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
import java.awt.Color
import java.awt.Font
import pl.burningice.plugins.image.file.ImageFile
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Engine for typing text on image
 *
 * @author pawel.gdula@burningice.pl
 */
class JaiTextEngine extends DefaultTextEngine {

    /**
     * Object representing image file
     *
     */
    BufferedImage fileToMark

    /**
     * Object representing image canvas
     *
     */
    Graphics2D graphics

    public JaiTextEngine(Color color, Font font, ImageFile loadedImage){
        super(color, font, loadedImage)
    }

    /**
     * Performs write actions
     *
     * @param text Text to type
     * @param deltaX Offset from left border of image
     * @param deltaY Offset from top border of image
     */
    void write(String text, int deltaX, int deltaY) {
        graphics.drawString(text, deltaX, deltaY);
    }

    /**
     * Returns write result
     *
     * @return BufferedImage objects representing current image
     */
    BufferedImage getResult(){
        graphics.dispose();
        fileToMark
    }

    protected void init() {
        fileToMark = ImageIO.read(loadedImage.inputStream);
        graphics = fileToMark.createGraphics();

        if (color) {
            graphics.setColor(color);
        }

        if (font) {
            graphics.setFont(font);
        }
    }
}
