package flabs.image

import flabs.image.repository.FileRepository
import flabs.image.web.WebVerticle
import flabs.image.worker.PngToJpegWorker
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import java.nio.file.Paths


fun main(vararg args: String) {
    val vertx = Vertx.vertx()
    println("Starting Image processing service")
    val imageRepo = FileRepository(Paths.get("/tmp/repo"), vertx)
    val actions = ImageActions(vertx)
    vertx.deployVerticle(WebVerticle(imageRepo, actions))


    vertx.deployVerticle({ PngToJpegWorker(imageRepo) }, DeploymentOptions()
            .setWorker(true)
            .setInstances(1)
        ) {println("Png to Jpg workers are deployed")}

    println("Image processing service started")

}