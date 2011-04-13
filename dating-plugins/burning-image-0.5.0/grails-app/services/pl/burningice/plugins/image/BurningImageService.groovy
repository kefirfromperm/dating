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
package pl.burningice.plugins.image

import org.springframework.web.multipart.MultipartFile
import  pl.burningice.plugins.image.engines.*

/**
 * Main entry for the plugin
 *
 * @author pawel.gdula@burningice.pl
 */
class BurningImageService {

    /**
     * This gets rid of exception for not using native acceleration
     */
    static {
        System.setProperty('com.sun.media.jai.disableMediaLib', 'true');
        System.setProperty('jmagick.systemclassloader', 'false');
    } 

    boolean transactional = false

    /**
     * Executes work for file determined by string path
     *
     * @param filePath Path where file is stored
     * @param resultDir Path to directory where output file should be save
     * @return Object that execute specified manipulations on image
     * @throws IllegalArgumentException If any input is null
     * @throws FileNotFoundException If there is no file in specified location or there is no output directory
     */
    def doWith(String filePath, String resultDir){
        if (!filePath || !resultDir) {
            throw new IllegalArgumentException('Source file and output directory paths must be provided')
        }

        def file = new File(filePath)

        if (!file.exists()) {
            throw new FileNotFoundException("There is no source file: ${filePath}")
        }

        getWorker(file, resultDir)
    }

    /**
     * Executes work for file determined by MultipartFile interface
     *
     * @param file File uploaded by the user (or in other case when file is represented by MultipartFile interface)
     * @param resultDir Path to directory where output file should be save
     * @return Object that execute specified manipulations on image
     * @throws IllegalArgumentException If any input is null
     * @throws FileNotFoundException If there is no file in specified location or there is no output directory
     */
    def doWith(MultipartFile file, String resultDir) {
        if (!file || !resultDir) {
            throw new IllegalArgumentException('Source file and output directory path must be provided')
        }

        if (file.isEmpty()) {
            throw new FileNotFoundException("Uploaded file ${file.originalFilename} is empty")
        }

        getWorker(file, resultDir)
    }

     /**
     * Executes work for file determined by MultipartFile interface
     *
     * @param file File uploaded by the user (or in other case when file is represented by MultipartFile interface)
     * @param resultDir Path to directory where output file should be save
     * @return Object that execute specified manipulations on image
     * @throws IllegalArgumentException If any input is null
     * @throws FileNotFoundException If there is no file in specified location or there is no output directory
     */
    def doWith(MultipartFile file) {
        if (!file) {
            throw new IllegalArgumentException('Uploaded image is null')
        }

        if (file.isEmpty()) {
            throw new FileNotFoundException("Uploaded file ${file.originalFilename} is empty")
        }

        new Worker(loadedImage:file)
    }

    /**
     * Create and configure object that execute specified manipulations on image
     *
     * @param file Image represented by different type of objects (File/MultipartFile)
     * @param resultDir Path to directory where output file should be save
     * @return Object that execute specified manipulations on image
     * @throws FileNotFoundException If there is no output directory
     */
    private def getWorker(file, resultDir){
        if (!(new File(resultDir).exists())) {
            throw new FileNotFoundException("There is no output ${resultDir} directory")
        }
        
        if (resultDir[-1] == '/'){
            resultDir = resultDir[0..-2]
        }

        new Worker(loadedImage:file, resultDir:resultDir)
    }
}
