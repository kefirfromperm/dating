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
package pl.burningice.plugins.image.validator

import org.springframework.web.multipart.MultipartFile
import pl.burningice.plugins.image.container.ContainerUtils
import pl.burningice.plugins.image.ast.intarface.ImageContainer

/**
 * Class allows to validate uploaded image associated with specified grails domain class.
 *
 * @author pawel.gdula@burningice.pl
 */
class ImageValidator {

    static def validate(MultipartFile image, ImageContainer container){
        def config = ContainerUtils.getConfig(container)
        //  there is no validation for this class
        if (!config?.constraints){
            return true
        }

        // nullable constraints
        if (!config.constraints.nullable
            && (!image || image.isEmpty())){
            return ['nullable']
        }
        // other validators require no null and not empty object
        if (!image || image.isEmpty()){
            return true
        }
        // maxSize constraint
        if (config.constraints.maxSize
            && config.constraints.maxSize < image.getSize()){
            return ['maxSize.exceeded', config.constraints.maxSize, image.getSize()]
        }
        // content type
        if (config.constraints.contentType
            && !config.constraints.contentType.contains(image.getContentType())){
            return ['contentType.invalid', config.constraints.contentType, image.getContentType()]
        }
        // all ok
        return true
    }
}