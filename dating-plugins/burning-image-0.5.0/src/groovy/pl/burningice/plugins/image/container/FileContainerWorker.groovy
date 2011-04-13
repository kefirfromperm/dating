/*
Copyright (c) 2010 Pawel Gdula <pawel.gdula@burningice.pl>

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
package pl.burningice.plugins.image.container

import pl.burningice.plugins.image.ResourcePathProvider

/**
 * Worker for File container
 *
 * @author pawel.gdula@burningice.pl
 */
class FileContainerWorker extends ContainerWorker {

    ResourcePathProvider resourcePathProvider 

    boolean hasImage() {
        container.imageExtension != null
    }

    void delete() {
        def path = resourcePathProvider.getPath(config.outputDir)
        config.images.each {subImageName, subImageOperations ->
            def file = new File("${path}/${ContainerUtils.getFullName(subImageName, container, config)}")
            if (file.exists()){
                file.delete()
            }
        }
    }

    SaveCommand getSaveCommand(String size) {
        new SaveToFileCommand(container, "${resourcePathProvider.getPath(config.outputDir)}/${ContainerUtils.getName(size, container, config)}")
    }
}
