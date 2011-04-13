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
package pl.burningice.plugins.image.engines

import pl.burningice.plugins.image.engines.scale.*

import java.awt.Font
import java.awt.Color
import pl.burningice.plugins.image.ConfigUtils
import pl.burningice.plugins.image.engines.watermark.WatermarkEngineFactory
import pl.burningice.plugins.image.engines.crop.CropEngineFactory
import pl.burningice.plugins.image.file.ImageFile
import pl.burningice.plugins.image.engines.text.TextEngine
import pl.burningice.plugins.image.engines.text.TextEngineFactory

/**
 * Object allows to build chains of action
 * It instance is pass as a parameter to closure that user define and
 * pass to execute method as parameter
 *
 * @author pawel.gdula@burningice.pl
 */
class Action {

    /**
     * Image that is set to manipulate
     *
     * @var ImageFile
     */
    ImageFile loadedImage

    /**
     * Name of output file
     * It is always return as a result of action
     *
     * @var String
     */
    def fileName

    /**
     * Method allows to scale image with approximate width and height
     * Width and height of image will never be greater than parameters width and height
     * but it could be lover (image could not be deformed)
     *
     * @param int width
     * @param int height
     * @throws IllegalArgumentException
     * @return String Name of output file
     */
    def scaleApproximate(width, height) {
        if (!width || !height
            || width <= 0 || height <= 0){
            throw new IllegalArgumentException("Scale width = ${width}, height = ${height} is incorrent")
        }

        loadedImage.update(ScaleEngineFactory.produceApproximateEngine(ConfigUtils.getEngine()).execute(loadedImage, width, height))
        fileName
    }

    /**
     * Method allows to scale image with accurate width and height
     * Width and height will be always (almost ;)) equals to set parameters
     * Image will no be deformed but first scaled and next cropped on the center
     * (if it will necessary)
     *
     * @param int width
     * @param int height
     * @throws IllegalArgumentException
     * @return String Name of output file
     */
    def scaleAccurate(width, height) {
        if (!width || !height
            || width <= 0 || height <= 0){
            throw new IllegalArgumentException("Scale width = ${width}, height = ${height} is incorrent")
        }

        loadedImage.update(ScaleEngineFactory.produceAccurateEngine(ConfigUtils.getEngine()).execute(loadedImage, width, height))
        fileName
    }

    /**
     * Method allows to add watermark to image
     * 
     * @param String watermarkPath Path to watermark image
     * @param [:] position Position on image where watermark should be placed (default [:])
     * @param float alpha Watermark alpha (default 1)
     * @throw IllegalArgumentException
     * @throw FileNotFoundException
     * @return String Name of output file
     */
    def watermark(watermarkPath, position = [:], alpha = 1f) {
        if (!watermarkPath
            || (position['left'] != null && position['right'] != null)
            || (position['top'] != null && position['bottom'] != null)){
            throw new IllegalArgumentException("Watermark watermarkPath = ${watermarkPath}, position = ${position}, alpha = ${alpha} is incorrect")
        }

        def watermarkFile = new File(watermarkPath)

        if (!watermarkFile.exists()){
            throw new FileNotFoundException("There is no ${watermarkPath} watermark file")
        }

        loadedImage.update(WatermarkEngineFactory.produceEngine(ConfigUtils.getEngine()).execute(watermarkFile, loadedImage, position, alpha))
        fileName
    }

    /**
     * Method allows to crop specified region from image
     *
     * @param deltaX Offset from left border of image
     * @param deltaY Offset from top border of image
     * @param width Size (horizontal) of crop region
     * @param width Size (vertical) of crop region
     * @throws IllegalArgumentException If any parameter is null,
     *                                  or delta is smaller than 0
     *                                  or crop region dimension is smaller or equal zero
     *                                  or crop region is beyond the image
     * @return String Name of output file
     */
    def crop(deltaX, deltaY, width, height){
        if (deltaX == null || deltaY == null || width == null || height == null){
            throw new IllegalArgumentException("Parameters cant be null: deltaX = ${deltaX}, deltaY = ${deltaY}, width = ${width}, height = ${height}")
        }

        if (deltaX < 0 || deltaY < 0 || width <= 0 || height <= 0){
            throw new IllegalArgumentException("Delta parameters smaller than 0, dimension smaller or equal 0: deltaX = ${deltaX}, deltaY = ${deltaY}, width = ${width}, height = ${height}")
        }

        def image = loadedImage.getSize()

        if (deltaX > image.width || deltaY > image.height
            || width > image.width || height > image.height
            || (deltaX + width) > image.width || (deltaY + height) > image.height){
            throw new IllegalArgumentException('Crop region not match to image size')
        }

        loadedImage.update(CropEngineFactory.produceEngine(ConfigUtils.getEngine()).execute(loadedImage, deltaX, deltaY, width, height))
        fileName
    }

    /**
     * Method allows to type text on image
     *
     * @param color Specified color of typed text
     * @param font Specified font of typed text
     * @param typist Type action
     * @return String Name of output file
     */
    def text(Color color, Font font, Closure typist){
        TextEngine engine = TextEngineFactory.produceEngine(ConfigUtils.getEngine(), color, font, loadedImage)
        typist(engine)
        loadedImage.update(engine.getResult())
        fileName
    }

    /**
     * Method allows to type text on image
     *
     * @param color Specified color of typed text
     * @param typist Type action
     * @return String Name of output file
     */
    def text(Color color, Closure typist){
        text(color, null, typist)
    }

    /**
     * Method allows to type text on image
     *
     * @param font Specified font of typed text
     * @param typist Type action
     * @return String Name of output file
     */
    def text(Font font, Closure typist){
        text(null, font, typist)
    }

    /**
     * Method allows to type text on image
     *
     * @param typist Type action
     * @return String Name of output file
     */
    def text(Closure typist){
        text(null, null, typist)
    }
}

