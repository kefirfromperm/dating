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

import javax.imageio.ImageIO
import pl.burningice.plugins.image.file.*
import pl.burningice.plugins.image.container.SaveCommand

/**
 * Execute actions on image
 *
 * @author pawel.gdula@burningice.pl
 */
class Worker {

  /**
   * Path to output directory
   *
   * @var String
   */
  private String resultDir

  /**
   * Object represents image to manipulate
   *
   * @var File/MultipartFile
   */
  private def loadedImage

  /**
   * Methods execute action on image
   * It use as a output file name name of original image
   *
   * @param Closure chain Chain of action on image
   * @return BurningImageService
   */
  Worker execute(Closure chain) {
    def image = ImageFileFactory.produce(loadedImage)
    return work("${resultDir}/${image.name}", image.name, chain, image)
  }

  /**
   * Methods execute action on image
   * Allows to specify output name by the user
   *
   * @param String Name of output image (without extension)
   * @param Closure chain Chain of action on image
   * @return Worker
   */
  Worker execute(String outputFileName, Closure chain) {
    def image = ImageFileFactory.produce(loadedImage)
    def fileName = "${outputFileName}.${image.extension}"
    return work("${resultDir}/${fileName}", fileName, chain, image)
  }

  /**
   * Methods execute action on image
   * Allows to specify output name by the user
   *
   * @param String Name of output image (without extension)
   * @param Closure chain Chain of action on image
   * @return Worker
   */
  Worker execute(SaveCommand saveCommand, Closure chain) {
    ImageFile image = ImageFileFactory.produce(loadedImage)
    chain(new Action(loadedImage: image))
    saveCommand.execute(image.getAsByteArray(), image.extension)
    return this
  }

  /**
   * Perform work
   *
   * @param outputFilePath Specify path to output image
   * @param fileName Specify file name
   * @param chain Specify work field
   * @param image ImageFile object representing image to manipulate
   * @return Self
   */
  private Worker work(outputFilePath, fileName, chain, ImageFile image) {
    chain(new Action(loadedImage: image, fileName: fileName))
    save(outputFilePath, image)
    this
  }

  /**
   * Save changed image
   * TODO: Check if this can be replaced by SaveCommand object
   *
   * @param outputFilePath Specify path to output image
   * @param image ImageFile object representing image to manipulate
   */
  private void save(String outputFilePath, ImageFile image) {
        ImageIO.write(ImageIO.read(new ByteArrayInputStream(image.getAsByteArray())), image.extension, new File(outputFilePath));
  }
}

