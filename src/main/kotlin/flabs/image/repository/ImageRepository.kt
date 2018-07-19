package flabs.image.repository

import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.core.streams.ReadStream
import java.awt.image.BufferedImage


data class ImageMeta (val fileId: String, val formats: Set<String> )

interface ImageRepository {
    fun dispose()
    fun listAll(): List<ImageMeta>
    fun newImage(name: String, readStr: ReadStream<Buffer>)
    fun getImage(name: String): Future<ReadStream<Buffer>>
    fun imagePresent(name: String, format: String): Boolean
    fun loadImage(name:String) : BufferedImage
    fun saveImage(img :BufferedImage, name: String)



}