package flabs.image.worker

import flabs.image.AsyncTest
import flabs.image.ImageActions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import org.junit.Before
import org.junit.Test


class DummyWorker : ImageWorker("dummy") {
    override fun applyAction(actionParams: JsonObject): Future<Boolean> {
        val res = Future.future<Boolean>()
        val action = actionParams.getString("action")

        if (action != null && "ok" == action) {
            res.complete(true)
        } else {
            res.fail(RuntimeException("Not ok Action"))
        }

        return res
    }

}

fun ImageActions.dummyAction(action: String): Future<Boolean> {
    return this.genericAction("dummy@img-worker", JsonObject().put("action", action))
}

class ImageWorkerTest : AsyncTest() {

    private var actions: ImageActions? = null
    private var vertx: Vertx? = null

    @Before
    fun setup() {
        vertx = rule.vertx()
        actions = ImageActions(vertx!!)
        vertx!!.deployVerticle(DummyWorker())
    }

    @Test
    fun testSuccessWorker(tc: TestContext) {

        actions!!.dummyAction("ok").setHandler { ar ->
            if (ar.succeeded()) {
                tc.assertTrue(ar.result())
            } else {
                tc.fail(ar.cause())
            }
        }
    }

    @Test
    fun testWorkerFailed(tc: TestContext) {

        actions!!.dummyAction("error").setHandler { ar ->

            if (ar.succeeded()) {
                tc.assertFalse(ar.result())
            } else {

                tc.assertEquals("Not ok Action", ar.cause().message)
            }
        }

    }

}