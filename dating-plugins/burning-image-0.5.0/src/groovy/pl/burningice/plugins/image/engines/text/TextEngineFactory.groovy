package pl.burningice.plugins.image.engines.text

import pl.burningice.plugins.image.engines.RenderingEngine
import java.awt.Font
import java.awt.Color
import pl.burningice.plugins.image.file.ImageFile

/**
 * Created by IntelliJ IDEA.
 * User: Gdulus
 * Date: 2010-07-07
 * Time: 22:55:54
 * To change this template use File | Settings | File Templates.
 */

class TextEngineFactory {

    public static TextEngine produceEngine(RenderingEngine engineType, Color color, Font font, ImageFile loadedImage){
        // Due problems with text writing on bmp images by using JMagick
        // ImageIO.write throws im==null exception
        // for BMP files we are using JAI engine even if IMAGE_MAGICK is selected 
        if (engineType == RenderingEngine.JAI
                || loadedImage.extension == ImageFile.BMP_EXTENSION){
            println "ceate JAI text engine for file type ${loadedImage.extension}"
            return new JaiTextEngine(color, font, loadedImage)
        }

        if (engineType == RenderingEngine.IMAGE_MAGICK
                && loadedImage.extension != ImageFile.BMP_EXTENSION){
            println "ceate IMAGE_MAGICK text engine for file type ${loadedImage.extension}"
            return new ImageMagickTextEngine(color, font, loadedImage)
        }

        throw new IllegalArgumentException("There is no text engine for type ${engineType}")
    }
}