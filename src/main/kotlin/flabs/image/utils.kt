package flabs.image

import flabs.image.repository.ImageMeta
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.JsonArray

fun String.rest(sym: Char): String {
    val index = this.indexOf(sym)
    return if (index < 0) {
        return this
    } else {
        this.substring(index + 1, this.length)
    }
}

fun ImageMeta.toJson(): JsonObject {
    return JsonObject()
            .put("imageId", fileId)
            .put("format", JsonArray(*formats.toList().toTypedArray()))
}