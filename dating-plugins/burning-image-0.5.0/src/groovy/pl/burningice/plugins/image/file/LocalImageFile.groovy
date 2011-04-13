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

package pl.burningice.plugins.image.file

import com.sun.media.jai.codec.*

/**
 * Representing files stored locally on the disk
 *
 * @author pawel.gdula@burningice.pl
 */
class LocalImageFile extends ImageFile {

    /**
     * Class constructor
     *
     * @param source Local file that is source image for this class
     * @return LocalImageFile
     */
    def LocalImageFile(File source) {
        super(source.name, fileToByteArray(source))
    }

    private static byte[] fileToByteArray(File source){
        FileInputStream stream = new FileInputStream(source);
        byte[] sourceContent = new byte[(int)source.length()];
        stream.read(sourceContent);
        return sourceContent; 
    }
}