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
package pl.burningice.plugins.image.container

import pl.burningice.plugins.image.ast.intarface.DBImageContainer
import pl.burningice.plugins.image.ast.intarface.FileImageContainer
import pl.burningice.plugins.image.ast.intarface.ImageContainer
import pl.burningice.plugins.image.ResourcePathProvider

/**
 * Factory for container workers
 *
 * @author pawel.gdula@burningice.pl
 */
class ContainerWorkerFactory {

    ResourcePathProvider resourcePathProvider

    /**
        * Method will produce worker based on passed image container
        *
        * @param container Image container for witch we want to get worker
        */
    public ContainerWorker produce(ImageContainer container){
        if (container instanceof DBImageContainer){
            return new DbContainerWorker(container:container)
        }

        if (container instanceof FileImageContainer){
            return new FileContainerWorker(container:container, resourcePathProvider:resourcePathProvider)
        }

        throw new IllegalArgumentException("There is no upload worker for container ${container}")
    }
}
