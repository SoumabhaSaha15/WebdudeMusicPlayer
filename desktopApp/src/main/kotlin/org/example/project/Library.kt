package org.example.project

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.Locale
import java.util.prefs.Preferences

data class Track(
    val path: Path,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int?,
)

data class ScanResult(
    val tracks: List<Track>,
    val skippedPaths: List<Path>,
    val skippedPathCount: Int,
)

class LibraryRepository {
    private val preferences = Preferences.userNodeForPackage(LibraryRepository::class.java)

    fun savedFolder(): Path? =
        preferences.get(FOLDER_PREFERENCE, null)?.let { Path.of(it) }

    fun saveFolder(path: Path) {
        preferences.put(FOLDER_PREFERENCE, path.toAbsolutePath().normalize().toString())
    }

    suspend fun scan(folder: Path, onProgress: (Int) -> Unit = {}): ScanResult {
        val root = folder.toRealPath()
        require(Files.isDirectory(root) && Files.isReadable(root)) {
            "The selected location is not a readable folder."
        }

        val tracks = linkedMapOf<Path, Track>()
        val skippedPaths = mutableListOf<Path>()
        var skippedPathCount = 0

        Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (attrs.isSymbolicLink) {
                    skippedPathCount++
                    if (skippedPaths.size < MAX_REPORTED_PATHS) {
                        skippedPaths.add(file)
                    }
                    return FileVisitResult.CONTINUE
                }
                if (!attrs.isRegularFile || !isMp3(file)) return FileVisitResult.CONTINUE

                try {
                    val realPath = file.toRealPath()
                    if (!realPath.startsWith(root)) {
                        skippedPathCount++
                        return FileVisitResult.CONTINUE
                    }
                    tracks.putIfAbsent(realPath, readTrack(realPath))
                } catch (_: IOException) {
                    skippedPathCount++
                    if (skippedPaths.size < MAX_REPORTED_PATHS) {
                        skippedPaths.add(file)
                    }
                } catch (_: SecurityException) {
                    skippedPathCount++
                    if (skippedPaths.size < MAX_REPORTED_PATHS) {
                        skippedPaths.add(file)
                    }
                }
                if (tracks.size % PROGRESS_INTERVAL == 0) onProgress(tracks.size)
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path, exception: IOException): FileVisitResult {
                skippedPathCount++
                if (skippedPaths.size < MAX_REPORTED_PATHS) {
                    skippedPaths.add(file)
                }
                return FileVisitResult.CONTINUE
            }
        })

        onProgress(tracks.size)
        return ScanResult(
            tracks = tracks.values.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }),
            skippedPaths = skippedPaths,
            skippedPathCount = skippedPathCount,
        )
    }

    private fun readTrack(path: Path): Track {
        val fallbackTitle = path.fileName.toString().substringBeforeLast('.', path.fileName.toString())
        return try {
            val audioFile = AudioFileIO.read(path.toFile())
            val tag = audioFile.tag
            Track(
                path = path,
                title = tag?.getFirst(FieldKey.TITLE)?.takeIf(String::isNotBlank) ?: fallbackTitle,
                artist = tag?.getFirst(FieldKey.ARTIST)?.takeIf(String::isNotBlank) ?: UNKNOWN_ARTIST,
                album = tag?.getFirst(FieldKey.ALBUM)?.takeIf(String::isNotBlank) ?: UNKNOWN_ALBUM,
                durationSeconds = audioFile.audioHeader.trackLength.takeIf { it > 0 },
            )
        } catch (_: CannotReadException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: InvalidAudioFrameException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: ReadOnlyFileException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: TagException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: IOException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: SecurityException) {
            fallbackTrack(path, fallbackTitle)
        } catch (_: RuntimeException) {
            fallbackTrack(path, fallbackTitle)
        }
    }

    private fun fallbackTrack(path: Path, title: String) = Track(
        path = path,
        title = title,
        artist = UNKNOWN_ARTIST,
        album = UNKNOWN_ALBUM,
        durationSeconds = null,
    )

    private fun isMp3(path: Path): Boolean =
        path.fileName.toString().substringAfterLast('.', "").lowercase(Locale.ROOT) == "mp3"

    private companion object {
        const val FOLDER_PREFERENCE = "music_folder"
        const val PROGRESS_INTERVAL = 25
        const val MAX_REPORTED_PATHS = 5
        const val UNKNOWN_ARTIST = "Unknown artist"
        const val UNKNOWN_ALBUM = "Unknown album"
    }
}
