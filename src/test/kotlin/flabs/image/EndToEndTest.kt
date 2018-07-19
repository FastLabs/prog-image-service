package flabs.image

import flabs.image.repository.FileRepository
import flabs.image.web.WebVerticle
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.file.Files

class EndToEndTest : AsyncTest() {

    var imgRepo: FileRepository? = null

    @Before
    fun setup() {
        val tmp = Files.createTempDirectory("test-img-repo")
        imgRepo = FileRepository(tmp.toAbsolutePath(), rule.vertx())
        initializeRepository()
    }

    @After
    fun tearDown() {
        imgRepo!!.dispose()
    }

    private fun initializeRepository() {
        imgRepo!!.copyClassPathResource("images/Superhero.png")
    }

    private fun validateResult(async: Async, tc: TestContext, webClient: WebClient) {
        webClient.get(2000, "localhost", "/image")
                .`as`(BodyCodec.jsonArray())
                .send { res ->
                    if (res.succeeded()) {
                        val httpResp = res.result()
                        if (httpResp.statusCode() != 200) {
                            tc.fail("Expecting status code 200 got instead ${httpResp.statusCode()}")
                        }
                        val files = httpResp.body()
                        tc.assertEquals(2, files.size())
                        println("All good with status code ${res.result().statusCode()}")
                        println(res.result().body())
                        async.complete()
                    } else {
                        tc.fail(res.cause())
                    }
                }
    }


    @Test
    fun simpleTest(tc: TestContext) {
        val async = tc.async()
        val vertx = rule.vertx()
        val webClient = WebClient.create(vertx)
        val actions = ImageActions(vertx)

        val web = WebVerticle(imgRepo!!, actions)

        vertx.deployVerticle(web) {
            imgRepo!!.getImage("Superhero.png").setHandler { res ->

                webClient.post(2000, "localhost", "/image")
                        .putHeader("Content-Type", "image/png")
                        .sendStream(res.result()) { r ->
                            if (r.succeeded()) {
                                println("Uploaded ${r.result().statusCode()}")
                                validateResult(async, tc, webClient)
                            } else {
                                println("Error when saving the image ${r.cause()}")
                                tc.fail(r.cause())
                            }
                        }
            }


        }


    }

}