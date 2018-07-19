package flabs.image.repository

import flabs.image.AsyncTest
import flabs.image.copyClassPathResource
import io.vertx.ext.unit.TestContext
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Files


class FileRepositoryTest : AsyncTest() {

    @Test
    fun testSingleFormat(tc: TestContext) {
        val tmp = Files.createTempDirectory("test-img-repo")
        val repo = FileRepository(tmp.toAbsolutePath(), rule.vertx())
        repo.copyClassPathResource("data/simple.file")
        assertEquals(listOf(ImageMeta(fileId = "simple", formats = setOf("file"))), repo.listAll())
        repo.dispose()
    }


    @Test
    fun testMultipleFormat(tc: TestContext) {
        val tmp = Files.createTempDirectory("test-img-repo")
        val repo = FileRepository(tmp.toAbsolutePath(), rule.vertx())
        repo.copyClassPathResource("data/double.f1")
        repo.copyClassPathResource("data/double.f2")
        assertEquals(listOf(ImageMeta(fileId = "double", formats = setOf("f1", "f2"))), repo.listAll())
        repo.dispose()
    }

}