package flabs.image.web

import flabs.image.repository.ImageRepository
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

fun handleHealth(rc: RoutingContext) {
    val rsp = rc.response()
    rsp.putHeader("content-type", "application/json")
    rsp.end(JsonObject().put("isAvailable", true).encodePrettily())
}


class WebVerticle(imgRepo: ImageRepository) : AbstractVerticle() {
    private val imgRepoHandlers = ImgRepoHttpHandlers(imgRepo)

    override fun start(startFuture: Future<Void>?) {
        val httpPort = 2000
        val router = Router.router(vertx)
        router.get("/health").handler(::handleHealth)
        router.get("/image/:fileName").handler(imgRepoHandlers.imageHandler)
        router.get("/image").handler(imgRepoHandlers.imageMetaHandler)
        router.post("/image").handler(imgRepoHandlers.imageUploadHandler)

        val server = vertx.createHttpServer()
        println("Listening on port $httpPort")
        server
                .requestHandler { router.accept(it) }
                .listen(httpPort)
        println("Web Server started")
        startFuture!!.complete()
    }

    override fun stop(stopFuture: Future<Void>?) {
        println("Web Server stopped")
        stopFuture!!.complete()
    }
}

