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

import javax.imageio.ImageIO
import pl.burningice.plugins.image.ast.intarface.FileImageContainer

/**
 * Provide usability of saving images in file
 *
 * @author pawel.gdula@burningice.pl
 */
class SaveToFileCommand implements SaveCommand {

    private FileImageContainer container

    private String outputFilePath

    SaveToFileCommand(FileImageContainer container, String outputFilePath){
        this.container = container
        this.outputFilePath = outputFilePath
    }

    void execute(byte[] source, String extension) {
        ImageIO.write(ImageIO.read(new ByteArrayInputStream(source)), extension, new File("${outputFilePath}.${extension}"));
        // this will updated for every copy of image, but it will be always the same
        // we must set this to mark container that image is saved
        // and there can be situation when only one image will be produced
        this.container.imageExtension = extension
    }
}
