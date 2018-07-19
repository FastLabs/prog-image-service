package flabs.image.web

import flabs.image.ImageActions
import flabs.image.repository.ImageRepository
import flabs.image.rest
import flabs.image.toJson
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer


import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.Pump
import io.vertx.core.streams.ReadStream
import io.vertx.ext.web.RoutingContext
import java.util.*

class ImgRepoHttpHandlers(private val imgRepo: ImageRepository, private val actions: ImageActions) {
    private val supportedContentType = setOf("image/png", "image/jpeg")

    val imageUploadHandler: (rc: RoutingContext) -> Unit = { rc ->
        val request = rc.request()
        val headers = request.headers()
        val response = rc.response()

        val contentType = headers.get("Content-Type")
        if (contentType == null || !supportedContentType.contains(contentType)) {
            response
                    .setStatusCode(415)
                    .setStatusMessage("Image Type not allowed")
                    .end()

        } else {
            val uuid = UUID.randomUUID().toString()
            val extension = contentType.rest('/')
            val fileName = "img-$uuid.$extension"
            imgRepo.newImage(fileName, request)

            request.endHandler {
                println("content done")
            }

            response
                    .setStatusCode(201)
                    .end(JsonObject().put("file", fileName).encodePrettily())
        }
    }

    val imageHandler: (rc: RoutingContext) -> Unit = { rc ->
        val fileName = rc.request().getParam("fileName")
        val (name, format) = fileName.split(".")
        println("Extracting image $fileName")
        if (fileName == null) {
            //todo: handle this
        } else {
            val response = rc.response()
            if (!imgRepo.imagePresent(name, format)) {
                println("Error: file not found $fileName")
                response.setStatusCode(404)
                        .setStatusMessage("Not Found $fileName")
                        .end()
            } else {
                response.headers().add("Content-Type", "image/$format")
                response.isChunked = true
                imgRepo.getImage(fileName)
                        .setHandler { ar ->
                            if (ar.succeeded()) {
                                val readStream = ar.result()
                                Pump.pump(readStream, rc.response()).start()
                                readStream.endHandler { response.end() }
                            } else {

                            }
                        }
            }
        }
    }

    val imageMetaHandler: (rc: RoutingContext) -> Unit = { rc ->
        val rsp = rc.response()
        rsp.putHeader("content-type", "application/json")
        val res = imgRepo.listAll()
                .map { it.toJson() }

        rsp.end(JsonArray(res).encodePrettily())
    }
}