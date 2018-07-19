package flabs.image.worker

import flabs.image.repository.ImageRepository
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import java.awt.image.BufferedImage


/**
 * Blocking actions, will need to work in a allocated worker thread
 */

class PngToJpegWorker(private val imgRepo: ImageRepository) : ImageWorker("png->jpg") {

    override fun applyAction(actionParams: JsonObject): Future<Boolean> {
        val f = Future.future<Boolean>()
        val imageName = actionParams.getString("name")

        if (imageName == null) {
            f.fail(RuntimeException("Image name parameter is not present"))
        } else {
            if (!imgRepo.imagePresent(imageName, "png")) {
                println("Error: unable to locate $imageName.png")
                f.fail(RuntimeException("File $imageName.png not present"))
            } else {
                println("Requested png to jpg conversion for $imageName")
                val img = imgRepo.loadImage("$imageName.png")
                val newImage = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
                newImage.createGraphics().drawImage(img, 0, 0, null)
                imgRepo.saveImage(newImage, "$imageName.jpg")
                println("Conversion complete for $imageName")
                f.complete()
            }
        }

        return f
    }


}