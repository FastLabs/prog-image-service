package flabs.image.worker

import io.vertx.core.Future
import io.vertx.core.json.JsonObject

class PngToJpegWorker : ImageWorker("png->jpg") {

    override fun applyAction(actionParams: JsonObject): Future<Boolean> {
        val f = Future.future<Boolean>()
        println("Requested png to jpg conversion")
        f.complete(true)
        return f
    }


}