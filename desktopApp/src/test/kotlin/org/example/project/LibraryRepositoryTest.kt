package org.example.project

import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LibraryRepositoryTest {
    @Test
    fun recursivelyFindsMp3FilesCaseInsensitivelyAndSortsByTitle() = runBlocking {
        val root = Files.createTempDirectory("webdude-library")
        try {
            val nested = Files.createDirectories(root.resolve("Artist/Album"))
            Files.write(nested.resolve("zeta.MP3"), byteArrayOf())
            Files.write(root.resolve("alpha.mp3"), byteArrayOf())
            Files.write(root.resolve("not-a-track.txt"), byteArrayOf())

            val result = LibraryRepository().scan(root)

            assertEquals(listOf("alpha", "zeta"), result.tracks.map { it.title })
            assertEquals(0, result.skippedPathCount)
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun unreadableTagsUseFilenameFallback() = runBlocking {
        val root = Files.createTempDirectory("webdude-library")
        try {
            Files.write(root.resolve("unknown.mp3"), byteArrayOf(1, 2, 3))

            val result = LibraryRepository().scan(root)

            assertEquals(1, result.tracks.size)
            assertEquals("unknown", result.tracks.single().title)
            assertEquals("Unknown artist", result.tracks.single().artist)
            assertEquals("Unknown album", result.tracks.single().album)
            assertFalse(result.tracks.single().path.toString().isBlank())
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun scannerDoesNotFollowDirectorySymlinks() = runBlocking {
        val root = Files.createTempDirectory("webdude-library")
        val outside = Files.createTempDirectory("webdude-outside")
        try {
            Files.write(outside.resolve("outside.mp3"), byteArrayOf())
            val link = root.resolve("linked")
            try {
                Files.createSymbolicLink(link, outside)
            } catch (_: UnsupportedOperationException) {
                return@runBlocking
            } catch (_: SecurityException) {
                return@runBlocking
            } catch (_: java.io.IOException) {
                return@runBlocking
            }

            val result = LibraryRepository().scan(root)

            assertTrue(result.tracks.isEmpty())
            assertEquals(1, result.skippedPathCount)
        } finally {
            root.toFile().deleteRecursively()
            outside.toFile().deleteRecursively()
        }
    }
}
