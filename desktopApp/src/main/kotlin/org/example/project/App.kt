package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileSystemView

@Composable
fun App() {
    val repository = remember { LibraryRepository() }
    val scope = rememberCoroutineScope()
    var folder by remember { mutableStateOf<Path?>(null) }
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var scannedCount by remember { mutableIntStateOf(0) }
    var skippedCount by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasCompletedScan by remember { mutableStateOf(false) }

    fun scan(path: Path, persistSelection: Boolean) {
        scope.launch {
            isScanning = true
            hasCompletedScan = false
            scannedCount = 0
            skippedCount = 0
            errorMessage = null
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.scan(path) { count ->
                        scope.launch(Dispatchers.Main) { scannedCount = count }
                    }
                }
                folder = path.toRealPath()
                tracks = result.tracks
                skippedCount = result.skippedPathCount
                if (persistSelection) repository.saveFolder(path)
                hasCompletedScan = true
            } catch (exception: IOException) {
                errorMessage = exception.message ?: "The selected folder could not be read."
            } catch (exception: SecurityException) {
                errorMessage = exception.message ?: "Permission to read the selected folder was denied."
            } catch (exception: IllegalArgumentException) {
                errorMessage = exception.message ?: "Choose a readable music folder."
            } finally {
                isScanning = false
            }
        }
    }

    fun chooseFolder() {
        val selected = chooseMusicFolder()
        if (selected != null) scan(selected, persistSelection = true)
    }

    LaunchedEffect(Unit) {
        val saved = repository.savedFolder()
        if (saved == null) {
            hasCompletedScan = false
        } else if (Files.isDirectory(saved) && Files.isReadable(saved)) {
            scan(saved, persistSelection = false)
        } else {
            folder = saved
            errorMessage = "The saved music folder is unavailable. Choose it again or select another folder."
        }
    }

    val visibleTracks = remember(tracks, searchQuery) {
        val query = searchQuery.trim()
        if (query.isEmpty()) tracks else tracks.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            if (folder == null && !isScanning) {
                Onboarding(onChooseFolder = ::chooseFolder)
            } else {
                LibraryScreen(
                    folder = folder,
                    tracks = visibleTracks,
                    totalTrackCount = tracks.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    isScanning = isScanning,
                    scannedCount = scannedCount,
                    skippedCount = skippedCount,
                    hasCompletedScan = hasCompletedScan,
                    errorMessage = errorMessage,
                    onChooseFolder = ::chooseFolder,
                    onRescan = { folder?.let { scan(it, persistSelection = false) } },
                )
            }
        }
    }
}

@Composable
private fun Onboarding(onChooseFolder: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Webdude Music Player", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Choose a local music folder to build your offline MP3 library.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onChooseFolder) { Text("Choose music folder") }
    }
}

@Composable
private fun LibraryScreen(
    folder: Path?,
    tracks: List<Track>,
    totalTrackCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isScanning: Boolean,
    scannedCount: Int,
    skippedCount: Int,
    hasCompletedScan: Boolean,
    errorMessage: String?,
    onChooseFolder: () -> Unit,
    onRescan: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Your library", style = MaterialTheme.typography.headlineMedium)
                Text(folder?.toString().orEmpty(), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = onChooseFolder, enabled = !isScanning) { Text("Change folder") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onRescan, enabled = folder != null && !isScanning) { Text("Rescan") }
        }
        Spacer(Modifier.height(16.dp))
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        if (folder != null) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("Search title, artist, or album") },
                singleLine = true,
            )
        }
        Spacer(Modifier.height(12.dp))
        when {
            isScanning -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(if (scannedCount > 0) "Scanning… $scannedCount tracks found" else "Scanning music folder…")
            }
            hasCompletedScan && totalTrackCount == 0 -> Text(
                "No MP3 files found. Choose another folder or rescan after adding music.",
                style = MaterialTheme.typography.bodyLarge,
            )
            hasCompletedScan && skippedCount > 0 -> Text(
                "Library loaded; $skippedCount path(s) could not be scanned, including skipped symbolic links.",
                color = MaterialTheme.colorScheme.error,
            )
            else -> Text("${tracks.size} track(s)", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(tracks, key = { it.path.toString() }) { track ->
                TrackRow(track)
            }
        }
    }
}

@Composable
private fun TrackRow(track: Track) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(track.title, fontWeight = FontWeight.SemiBold)
                Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodySmall)
            }
            track.durationSeconds?.let {
                Text(formatDuration(it), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun formatDuration(seconds: Int): String = "%d:%02d".format(seconds / 60, seconds % 60)

private fun chooseMusicFolder(): Path? {
    var selectedPath: Path? = null
    val chooser = {
        val dialog = JFileChooser(FileSystemView.getFileSystemView().defaultDirectory).apply {
            dialogTitle = "Choose your music folder"
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedPath = dialog.selectedFile.toPath()
        }
    }
    if (SwingUtilities.isEventDispatchThread()) chooser() else SwingUtilities.invokeAndWait(chooser)
    return selectedPath
}
