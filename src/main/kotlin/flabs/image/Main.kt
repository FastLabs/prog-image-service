package flabs.image

import flabs.image.repository.FileRepository
import flabs.image.web.WebVerticle
import io.vertx.core.Vertx
import java.nio.file.Paths


fun main(vararg args: String) {
    val vertx = Vertx.vertx()
    println("Starting Image processing service")
    val imageRepo = FileRepository(Paths.get("/tmp/repo"), vertx)
    vertx.deployVerticle(WebVerticle(imageRepo))
    println("Image processing service started")

}