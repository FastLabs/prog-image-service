package flabs.image.repository

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.OpenOptions
import io.vertx.core.streams.Pump
import io.vertx.core.streams.ReadStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList


class FileRepository(val location: Path, private val vertx: Vertx) : ImageRepository {


    override fun getImage(name: String): Future<ReadStream<Buffer>> {
        val resFut = Future.future<ReadStream<Buffer>>()
        vertx.fileSystem().open("${this.location}/$name", OpenOptions().setRead(true)) { res ->
            if (res.succeeded()) {
                resFut.complete(res.result())
            } else {
                resFut.fail(res.cause())
            }
        }
        return resFut
    }

    override fun newImage(name: String, readStr: ReadStream<Buffer>) {
        readStr.pause()
        vertx.fileSystem().open("${this.location}/$name", OpenOptions()) { res ->
            if (res.succeeded()) {
                val destination = res.result()
                Pump.pump(readStr, destination).start()
                readStr.resume()
                destination.endHandler {
                    println("File $name stored")
                }

            } else {
                println("Could not upload the file ${res.cause()}")
            }
        }

    }

    private fun getImageMeta(groupEntry: Map.Entry<String, List<Pair<String, String>>>): ImageMeta {
        val formats = groupEntry.value.map { it.second }
        return ImageMeta(fileId = groupEntry.key, formats = HashSet(formats))
    }

    private fun splitFileName(path: Path): Pair<String, String> {
        val name = path.fileName.toString()
        val sepIndex = name.lastIndexOf('.')

        return Pair(first = name.substring(0, sepIndex), second = name.substring(sepIndex + 1, name.length))
    }

    override fun listAll(): List<ImageMeta> {
        return Files.list(location)
                .toList()
                .map { splitFileName(it) }
                .filter { !it.first.isBlank() }
                .groupBy { it.first }
                .map { getImageMeta(it) }
    }


    override fun dispose() {
        println("Disposing image repository $location")
        Files.list(this.location).forEach(Files::delete)
        Files.deleteIfExists(this.location)
    }
}