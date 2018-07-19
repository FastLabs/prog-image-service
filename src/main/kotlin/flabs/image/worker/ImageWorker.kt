package flabs.image.worker

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

abstract class ImageWorker(actionName: String) : AbstractVerticle() {
    private val workerName = "$actionName@img-worker"

    override fun start() {

        vertx.eventBus().consumer<JsonObject>(workerName) { msg ->
            applyAction(msg.body()).setHandler { ar ->
                if (ar.succeeded()) {
                    msg.reply(JsonObject().put("status", "ok"))
                } else {
                    msg.reply(JsonObject().put("status", "error"))
                }
            }

        }
        println("Worker $workerName initialized")

    }

    abstract fun applyAction(actionParams: JsonObject): Future<Boolean>
}