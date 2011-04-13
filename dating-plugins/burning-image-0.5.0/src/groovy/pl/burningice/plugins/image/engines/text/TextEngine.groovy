package pl.burningice.plugins.image.engines.text

import java.awt.image.BufferedImage

/**
 * Created by IntelliJ IDEA.
 * User: Gdulus
 * Date: 2010-07-07
 * Time: 23:06:09
 * To change this template use File | Settings | File Templates.
 */
public interface TextEngine {

    void write(String text, int deltaX, int deltaY) 

    BufferedImage getResult()
}