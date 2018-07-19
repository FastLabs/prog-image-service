package flabs.image.worker

import flabs.image.AsyncTest
import flabs.image.ImageActions
import flabs.image.copyClassPathResource
import flabs.image.repository.FileRepository
import io.vertx.ext.unit.TestContext
import org.junit.Test
import java.nio.file.Files

class PngToJpegWorkerTest : AsyncTest() {


    @Test
    fun testConversion(tc: TestContext) {
        val async = tc.async()
        val vertx = rule.vertx()
        //val imgRepo = FileRepository(Paths.get("/tmp/repo"), vertx)
        val tmp = Files.createTempDirectory("test-img-repo")
        val imgRepo = FileRepository(tmp.toAbsolutePath(), vertx)
        imgRepo.copyClassPathResource("images/Superhero.png")
        val imageActions = ImageActions(vertx)
        vertx.deployVerticle(PngToJpegWorker(imgRepo)) {
            imageActions.transformPngToJpeg("Superhero")
                    .setHandler { res ->
                        if (res.succeeded()) {
                            tc.assertTrue(imgRepo.imagePresent("Superhero", "jpg"))
                            imgRepo.dispose()
                            async.complete()

                        } else {
                            tc.fail(res.cause())
                            imgRepo.dispose()
                        }
                    }
        }
    }

    @Test
    fun xx () {
        println("test.xx".split("."))
    }

}