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

import java.awt.image.renderable.ParameterBlock
import javax.media.jai.*;

/**
 * Class allows to scale image with approximate width and height
 * Result image will not contain exact width and height given by user
 * if there will be image deformation
 *
 * @author Pawel Gdula <pawel.gdula@burningice.pl>
 */
private class JaiApproximateScaleEngine extends JaiScaleEngine {

    /**
     * @see JaiScaleEngine#scaleImage
     */
    protected def scaleImage(image, width, height) {
        if (width > image.width
            && height > image.height){
            return image 
        }

        def scaleX = width / image.width
        def scaleY = height / image.height
        resize(scaleX > scaleY ? scaleY : scaleX)(image)
    }

    /**
     * We provide two types of image resize to eliminate situation
     * when scale is > 1, for such action SubsampleAverage throw exception
     * For this situation we resize image by "scale"
     *
     * @param Float scale
     * @return Closure
     */
    protected def resize(scale){
        (scale > 1 ? resizeByScale : resizeBySubsampleAverage).curry(scale)
    }

    /**
     * Resize option for scale > 1
     *
     * @param scale Scale parameter
     * @param image Image to scale
     * @return RenderedOp
     */
    protected def resizeByScale = {scale, image ->
        def scaleParams = new ParameterBlock();
        scaleParams.addSource(image);
        scaleParams.add((float)scale);
        scaleParams.add((float)scale);
        scaleParams.add(0.0f);
        scaleParams.add(0.0f);
        scaleParams.add(new InterpolationNearest());
        JAI.create('scale', scaleParams, null);
    }

    /**
     * Resize option for scale <= 1
     *
     * @param scale Scale parameter
     * @param image Image to scale
     * @return RenderedOp
     */
    protected def resizeBySubsampleAverage = {scale, image ->
        def scaleParams = new ParameterBlock();
        scaleParams.addSource(image);
        scaleParams.add((double)scale);
        scaleParams.add((double)scale);
        scaleParams.add(new InterpolationNearest());
        JAI.create('SubsampleAverage', scaleParams, null);
    }
}