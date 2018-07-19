package flabs.image

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class ImageActions(vertx: Vertx) {
    private val eb = vertx.eventBus()


    fun genericAction(actionName: String, actionParams: JsonObject): Future<Boolean> {
        val actionResult = Future.future<Boolean>()
        eb.send(actionName, actionParams) { x: AsyncResult<Message<JsonObject>> ->
            if (x.succeeded()) {
                val jsonRes = x.result().body()
                val status = jsonRes.getString("status")
                if ("ok" == status) {
                    actionResult.complete(true)
                } else {
                    //TODO: fail here
                    actionResult.complete(false)
                }
            } else {
                actionResult.fail(x.cause())
            }
        }

        return actionResult
    }

    fun transpformPngToJpeg(name: String): Future<Boolean> {
        return genericAction("png->jpg@img-worker", JsonObject().put("imgName", name))
    }

}