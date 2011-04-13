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

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import pl.burningice.plugins.image.file.ImageFile

/**
 * Engine for typing text on image
 *
 * @author pawel.gdula@burningice.pl
 */
abstract class DefaultTextEngine implements TextEngine{

    /**
     * Object representing image file
     *
     */
    protected Color color

    /**
     * Object representing image canvas
     *
     */
    protected  Font font

    /**
     * Object representing uploaded image
     *
     */
    protected  ImageFile loadedImage

    /**
     * Default class constructor
     *
     * @param color Representing current color of text. Can be null
     * @param font Representing current font of text. Can be null
     * @param loadedImage Image to type on it
     */
    public DefaultTextEngine(Color color, Font font, ImageFile loadedImage){
        this.color = color
        this.font = font
        this.loadedImage = loadedImage
        init()
    }

    /**
     * 
     *
     */
    abstract protected void init()

    /**
     * Performs write actions
     *
     * @param text Text to type
     * @param deltaX Offset from left border of image
     * @param deltaY Offset from top border of image
     */
    abstract void write(String text, int deltaX, int deltaY) 

    /**
     * Returns write result
     *
     * @return BufferedImage objects representing current image
     */
    abstract BufferedImage getResult()
}