package flabs.image

import flabs.image.repository.FileRepository
import io.vertx.ext.unit.junit.RunTestOnContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.FileOutputStream
import java.nio.file.Paths

fun FileRepository.copyClassPathResource(resourceName: String) {
    ClassLoader.getSystemResourceAsStream(resourceName)
            .use { input ->
                val destination = Paths.get(resourceName).fileName!!
                FileOutputStream(Paths.get(this.location.toString(), destination.toString()).toFile())
                        .use { output ->
                            do {
                                val x = ByteArray(100)
                                val r = input.read(x, 0, x.size)
                                if (r != -1) {
                                    output.write(x, 0, r)
                                }
                            } while (r != -1)
                        }
            }
}


@RunWith(VertxUnitRunner::class)
abstract class AsyncTest {

    @Rule
    @JvmField
    val rule = RunTestOnContext()
}