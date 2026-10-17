# Webdude Music Player

Webdude Music Player is a Kotlin/Compose Desktop application targeting the JVM.

## Current implementation

- First-launch music-folder selection and saved-folder restoration.
- Recursive MP3 discovery, with symbolic links skipped to keep scans inside the selected folder.
- Local ID3 metadata reading with filename and unknown-value fallbacks.
- Search across title, artist, and album; tracks are initially sorted by title.
- Scan progress, rescan, folder replacement, and basic unavailable-folder/error feedback.

This initial slice does **not** yet play audio or edit tags/lyrics. It does not modify music files.

## Run

On Windows:

```powershell
.\gradlew.bat :desktopApp:run
```

On macOS/Linux:

```shell
./gradlew :desktopApp:run
```

Run desktop tests with `:desktopApp:test` using the wrapper for your operating system.
