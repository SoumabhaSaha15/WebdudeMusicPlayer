# Webdude Music Player — Product Requirements and Technical Specification

**Status:** Living specification; initial library implementation started  
**Repository:** `SoumabhaSaha15/WebdudeMusicPlayer`  
**Initial release focus:** Offline MP3 playback and local library management  
**Last reviewed:** 2026-10-17

## 1. Project Overview

Webdude Music Player is planned as a desktop application for playing and managing a user's local MP3 collection. Its first release is intended to work offline after installation and keep the user's audio and library data on their device. The planned experience includes folder-based discovery, playback, ID3 tag editing, and embedded lyrics support.

This document distinguishes **verified repository facts**, **implemented behavior**, **planned requirements**, and **open decisions**.

## 2. Project Goals

- Provide reliable offline playback of MP3 files from a user-selected local folder.
- Make the local collection easy to browse, search, sort, and play.
- Allow careful editing of supported ID3 metadata and lyrics without damaging audio or unrelated tags.
- Restore useful application preferences and, where technically reliable, the previous playback session.
- Deliver a responsive, accessible desktop interface with clear recovery guidance.
- Keep the initial release focused; defer other formats and online features.

## 3. Scope and Non-Goals

### Initial release scope

- Desktop JVM application distributed for the repository's configured DMG, MSI, and DEB package formats.
- One user-selected music folder, MP3 discovery, local library browsing, playback controls, queue, search, and sorting.
- Reading and editing supported embedded ID3 fields and unsynchronized/synchronized lyrics.
- Local persistence for application preferences and any required library index.
- Offline operation with no mandatory account, cloud service, remote API, or telemetry.

### Non-goals for the initial release

- Streaming, online catalog or lyric-provider integrations, account synchronization, and cloud backup.
- Mobile, web, or additional audio-format support; the current configured target is desktop JVM only.
- Automatic destructive cleanup, audio-content editing/transcoding, or guaranteed recovery of arbitrary corrupt files.
- Promising background playback, global media keys, system audio-focus integration, or platform-specific media integrations before their desktop support is verified.

## 4. Existing Repository and Technology Stack

### Verified facts

| Area | Repository evidence |
|---|---|
| Build | Gradle Kotlin DSL; root project `WebdudeMusicPlayer`. |
| Language and target | Kotlin 2.4.20; `shared` configures a Kotlin Multiplatform JVM target. |
| UI | Compose Multiplatform 1.12.0 with Compose UI, Foundation, Runtime, and Material 3 dependency `1.12.0-alpha03`. |
| Application entry | `desktopApp` starts a Compose Desktop `Window`; the app screen and JVM-specific folder/library behavior are implemented in `desktopApp`. |
| Packaging | Compose Desktop native distribution formats are configured for DMG, MSI, and DEB; package version is `1.0.0`. |
| Other declared dependencies | Kotlin coroutines Swing, AndroidX Lifecycle Compose/runtime, and Jaudiotagger 3.0.1 are declared. |
| Existing implementation | Desktop onboarding/folder selection, saved-folder restoration using JVM Preferences, recursive MP3 scanning, basic ID3 reads, title sorting, and title/artist/album search. Symbolic links are skipped; malformed/unreadable tags fall back to filename and unknown artist/album. |
| Not yet implemented | Audio playback, artwork display, metadata or lyrics editing, synchronized lyrics, playback preferences, and a cached library index. |
| Existing tests | Desktop tests cover nested/case-insensitive MP3 discovery, malformed-tag fallback, and not following symbolic-link directories. Common/JVM scaffold tests remain examples. |
| Project documentation | `README.md` summarizes the current desktop slice and Gradle run/test tasks. |

The UI libraries establish **Compose Multiplatform** as the apparent meaning of “CMP” in the supplied request. The repository does not define that acronym explicitly; confirm that this is the intended meaning. The Material 3 artifact is an alpha dependency, so its declared version alone does not prove which current Material Design guidance or components are suitable for production. Confirm a stable, compatible component-library/version policy before implementation.

Jaudiotagger is currently used only to read basic track metadata. Its presence does not establish safe tag-write, artwork, or lyrics support. Audio playback, tag/lyrics editing, artwork display, and a local database/index remain unimplemented. Select further dependencies only after checking their JVM support, license, maintenance, offline behavior, and file-safety guarantees.

## 5. Product Principles

1. **Local-first:** music files remain under the user's control on local storage.
2. **Offline by default:** core workflows never depend on connectivity.
3. **Safe editing:** do not report a metadata or lyrics change as successful until it is durably written and re-read or otherwise verified.
4. **Clear state:** distinguish unavailable files, incomplete scans, unsaved edits, and absent metadata.
5. **Recoverable:** preserve user data and guide the user through permission, storage, and file errors.
6. **Incremental delivery:** build and validate the smallest dependable MP3 workflow before optional enhancements.

## 6. Target Users and Main Use Cases

- A desktop user with a local MP3 collection who wants to listen without an internet connection.
- A user who wants to find tracks by title, artist, or album and organize playback into a queue.
- A user correcting incomplete or inaccurate embedded tags.
- A user reading or maintaining lyrics stored in MP3 files.
- A user whose selected folder or a particular file has become unavailable and needs a clear recovery path.

## 7. Functional Requirements

Priorities are for the initial release: **Must** is release-critical, **Should** is valuable but may follow a dependable core, **Could** is optional, and **Won't** is explicitly excluded from this release. Requirements are intended to become test cases and implementation tasks.

| ID | Requirement and expected behavior | Priority |
|---|---|---|
| FR-001 | On first launch with no usable saved folder, show onboarding and let the user choose a music folder using an appropriate desktop folder-selection mechanism. | Must |
| FR-002 | Persist the selected folder reference and restore it after application restart when it remains valid and accessible. | Must |
| FR-003 | Detect a missing, moved, or inaccessible selected folder; explain the problem and offer folder reselection without crashing or silently discarding the library state. | Must |
| FR-004 | Let the user change the selected folder in Settings and confirm the effect on the current library/queue before replacing it. | Must |
| FR-005 | Discover supported `.mp3` files in the selected folder; handle case variation in extensions and do not assume files contain valid ID3 tags. | Must |
| FR-006 | Present discovered tracks with metadata fallbacks, artwork where readable, and useful browse, sort, and search behavior. | Must |
| FR-007 | Rescan without creating duplicate library entries; detect added, deleted, renamed, and modified files on a subsequent scan. | Must |
| FR-008 | Show scan progress and completion, including inaccessible subfolders/files and partial-scan errors. Allow a failed/interrupted scan to be retried. | Must |
| FR-009 | Play, pause, seek, change track, and show elapsed/total time for supported MP3 files. | Must |
| FR-010 | Provide a queue, next/previous behavior, track-end advancement, and clear behavior for repeat and shuffle modes. | Must |
| FR-011 | Provide volume and mute controls when supported by the selected playback implementation. Persist the chosen volume. | Should |
| FR-012 | Keep playback state while navigating within the application. Define and test behavior when closing/reopening the application. | Must |
| FR-013 | Read supported embedded ID3 fields and refresh displayed metadata after a successful edit. | Must |
| FR-014 | Edit supported ID3 fields and save changes to the original MP3 while preserving audio content and unrelated metadata where the chosen library permits. | Must |
| FR-015 | Read, display, edit, and save unsynchronized lyrics without losing line breaks or text encoding. | Must |
| FR-016 | Read and display synchronized lyrics against playback time; update the active line during playback. | Should |
| FR-017 | Where the chosen tag library safely supports it, edit synchronized lyric timestamps and text and save them without removing unrelated frames. | Should |
| FR-018 | Persist playback preferences and selected folder locally; store only the additional index/cache data needed for acceptable startup and browsing. | Must |
| FR-019 | Operate core library, playback, metadata, lyrics, and settings workflows without a network connection. | Must |
| FR-020 | Surface file, decoding, permission, storage, and save errors with an actionable message; never show false success. | Must |
| FR-021 | Provide keyboard-accessible navigation and controls; support hardware/global media keys only if verified on the target platforms. | Should |
| FR-022 | Support additional audio formats, remote lyric lookup, cloud sync, and streaming. | Won't |

## 8. Detailed User Journeys

### First launch and folder selection

1. The user opens the app with no saved folder.
2. The app explains that a local music folder is needed and that the initial release scans MP3 files.
3. The user chooses a folder. If selection is cancelled, remain on onboarding and do not create a misleading empty library.
4. The app validates access, saves the folder reference, and begins a scan with visible progress.
5. The app opens the library on completion. Empty folders, folders without MP3 files, and partial scan errors receive distinct explanatory states.

### Returning launch

1. Attempt to restore the saved folder.
2. If it is accessible, load the local index and validate/rescan according to the selected scan policy.
3. If inaccessible, show a recovery screen with the last known folder and a folder-selection action; do not repeatedly prompt after a user chooses to defer.

### Browse and play

1. The user browses, searches, or sorts tracks.
2. Selecting a track starts playback and places it in a documented queue position.
3. The persistent player surface shows track identity, artwork fallback, progress, and controls.
4. Navigating elsewhere does not stop playback. Track-end and repeat/shuffle settings determine subsequent playback according to the queue rules in Section 11.

### Edit tags or lyrics

1. The user opens a track's details/editor and sees the values currently read from the file.
2. The user edits supported values; validation errors are shown beside the affected field and do not alter the file.
3. Before writing, confirm only when the operation risks meaningful data loss or overwrites an unresolved conflict.
4. Write safely, report errors precisely, and refresh the library/editor from the resulting file only after successful persistence.

### Change folder

1. The user selects a new folder in Settings.
2. Validate that it is readable before changing the active root.
3. Explain whether the old library index is retained, discarded, or becomes inactive; resolve the queue/current-track policy before shipping.
4. Start a scan and preserve the previous selection if validating the replacement fails.

## 9. Application Navigation and Screen Specifications

The layout details below are proposals, not existing screens.

| Screen/surface | Required content and states |
|---|---|
| Onboarding / folder recovery | App purpose, folder selection action, selected/unavailable path when known, cancel/defer behavior, validation error, and retry. |
| Library | Search, sort/filter controls, track rows/cards, metadata fallbacks, scan status, and distinct loading, empty, no-supported-files, partial-error, and failure states. |
| Now Playing / persistent player | Current track, artwork or placeholder, play/pause, previous/next, seek position and duration, volume/mute, repeat/shuffle state, and queue access. |
| Queue | Ordered upcoming tracks, current item, removal/reordering if included, empty state, and behavior when an item becomes unavailable. |
| Track details / tag editor | Editable supported tags and artwork/lyrics sections; dirty/unsaved state, validation, save/cancel, and write-failure recovery. |
| Lyrics | Unsynchronized multiline text or synchronized timestamped lines, current-line highlight when synchronized, and empty/unreadable state. |
| Settings | Current folder/change-folder, playback preferences, scan options once decided, and locally stored preference controls. |

On narrow desktop windows, reflow or reduce secondary content without hiding essential playback controls. Confirm the minimum supported window dimensions and UI behavior with usability testing.

## 10. Music Library and File Discovery

### Discovery and metadata model

- Identify MP3 files by extension and, where practical, validate the file content/decoder result rather than trusting an extension alone.
- Decide whether to scan nested subfolders before implementation; see Section 25. Report inaccessible paths as partial failures and continue scanning other readable locations.
- Treat file path/identity, file-derived tags, and app-owned state as separate data. Never write a library correction back to a tag unless the user explicitly saves it.
- Preserve original tag values. For display, use a fallback such as embedded title → filename without extension; label fallback behavior consistently rather than pretending it came from the file.
- Read title, artist, album, album artist, track/disc number and totals, genre, year/recording date, duration, artwork, composer, and lyrics when present and supported by the selected libraries. Missing or malformed values must not prevent a playable file from appearing.
- Avoid duplicates by normalized absolute path within the selected root. File keys based on content hashes may be expensive and should not be required unless a tested need justifies them.
- On rescan, reconcile added, removed, moved/renamed, and modified files. Define whether moved files within a root retain app-owned state; do not infer identity solely from mutable tags.

### Scan lifecycle

Run filesystem traversal and metadata extraction off the UI thread, expose progress, and avoid holding the entire audio collection in memory. Make scans cancellable if the selected traversal and index design can safely support cancellation. An interrupted scan must leave a recoverable index and must not mark unseen files as deleted until a scan has completed successfully for the relevant locations.

**Proposed performance targets (validate on a documented reference desktop and test corpus):**

- Library UI becomes interactive within 2 seconds after opening a cached library of 10,000 tracks, excluding slow storage access.
- A first scan of 1,000 local MP3 files completes within 60 seconds on the reference machine; progress is visible during the scan.
- Search over 10,000 indexed tracks responds within 200 ms at the 95th percentile.

These are planning targets, not verified performance of the current scaffold. Revisit them after selecting the tag/decoder/index approach.

## 11. Audio Playback Requirements

### Essential behavior

- Play/pause the selected track and expose the same state to the UI.
- Seek by dragging/keyboard-operating the seek control; show current position and duration and tolerate unknown duration until available.
- Next/previous use a defined queue order. Previous should restart the current track when playback is beyond a defined threshold (proposed: 3 seconds); otherwise select the prior queue item. Confirm this interaction.
- At track end, advance to the next eligible queue item; stop cleanly at queue end when repeat is off.
- Repeat modes: off, repeat-one, repeat-all. Repeat-one replays the current item; repeat-all wraps at queue end.
- Shuffle changes selection order without losing queue items. Define whether toggling shuffle reshuffles upcoming tracks only or the whole queue and preserve the current item.
- If a file is missing, unreadable, or unsupported by the selected decoder, show an error for that track and provide an explicit continue/skip path; avoid an endless retry loop.
- Playback continues when moving between app screens. Closing the window/process ends playback unless a verified desktop background/close-to-tray feature is later adopted.

### Optional or platform-dependent capabilities

Global shortcuts, OS media-key integration, tray playback, background playback after window closure, audio-focus/interruption handling, gapless playback, crossfade, and restoration at the last sample-accurate position are not established by the current stack. Verify each separately on Windows, macOS, and Linux before promising it. They are not prerequisites for basic playback.

Persisting last track and position is a **Should** enhancement: restore only if the file is still available and the stored position is valid for its current duration. Do not auto-resume unexpectedly without a product decision; restoration may instead reopen a paused session.

## 12. ID3 Metadata Editing

### Supported fields and behavior

Provide editable title, artist, album, album artist, genre, year/recording date, track number and total, disc number and total, composer where supported, and embedded artwork where supported by the selected implementation. Display unsupported or unrecognized frames without silently deleting them during an edit.

### Safe write requirements

- Inspect the current tags and their versions before selecting the writer and establishing supported-field behavior.
- Validate numeric fields (track/disc numbers and totals, year/date) without rejecting legitimate values solely because they do not match a narrow UI assumption.
- Write to the original file only after an explicit save. Detect read-only files and permission/storage failures.
- Use a temporary-file/replace strategy or the tag library's documented safe update method when supported. Retain/recover the original if replacement fails; do not claim atomicity unless verified on each supported filesystem.
- Preserve audio frames and unrelated tags/frames. After writing, re-read the file and refresh the library from the persisted result.
- If the library cannot safely preserve an encountered tag/frame or artwork format, warn and avoid a destructive rewrite; disclose limitations in the editor.
- Offer a confirmation for potentially destructive operations such as replacing artwork or a write that may discard unsupported frames. Consider optional backup behavior only after its storage and retention rules are decided.

ID3v2.3 and ID3v2.4 are common MP3 tag versions, but the repository declares no ID3 library and therefore provides no evidence for which versions, encodings, frames, or preservation guarantees can be supported. Select and test a JVM-compatible library before committing to a version/compatibility promise. Keep original tag version where feasible; document any conversion behavior and verify interoperability using representative players and files.

## 13. Synchronized and Unsynchronized Lyrics

### Unsynchronized lyrics

- Read and write lyrics stored in an ID3 unsynchronized lyrics frame (`USLT`) when supported by the selected tag library.
- Preserve text, line endings, blank lines, and Unicode encoding. Show a multiline editor and retain unsaved changes if a save fails.
- If multiple language/descriptor variants exist, do not silently overwrite a different variant; show the selected variant or ask which one to edit.

### Synchronized lyrics

- Read and display timestamped lyrics from the ID3 synchronized lyrics frame (`SYLT`) where supported. `SYLT` has timing/content-type and timestamp-format semantics; ensure the parser maps timestamps to playback time correctly.
- Common `.lrc` syntax is a separate text/sidecar format, not itself an ID3 frame. Sidecar support is not assumed in the first release and requires a decision about filename matching, precedence, editing, and whether to write sidecars.
- Sort display by timestamp without changing stored content. Ignore or flag invalid timestamps visibly; define duplicate timestamps as simultaneous/adjacent lines rather than silently deleting either. Decide how untimed lines are displayed and whether out-of-order frame entries are normalized only in presentation.
- Highlight the line whose timestamp is the greatest timestamp less than or equal to current playback position; clear the highlight before the first timestamp and after playback stops.
- Update highlighting at least 4 times per second while synchronized lyrics are visible, subject to playback position updates and UI lifecycle.
- Editing synchronized lyrics is a Should feature. Saving must preserve unrelated metadata and accurately round-trip timestamps and text. If the selected library does not support safe `SYLT` writes, keep synchronized lyrics read-only and make that limitation explicit rather than using a lossy substitute.

When embedded lyric sources conflict, show source/variant metadata if available and never merge or replace them silently. The first release does not fetch lyrics online.

## 14. Offline-First Architecture

- Once installed and initial folder selection is complete, browsing, scanning, playback, tag/lyrics reading and editing, and settings must work without a network connection.
- Do not add mandatory online services, remote metadata/lyrics requests, authentication, cloud storage, or telemetry.
- Keep audio files in place. Any app-owned index, preferences, artwork cache, or session state must be stored locally in an application-appropriate location and must be rebuildable where practical.
- Do not treat cached metadata as authoritative after an external file change; reconcile it through rescanning or file-change detection.
- Packaging must include the runtime/dependencies required for offline use, subject to platform packaging verification.
- Optional future network functionality must be disabled by default, separately scoped, and must not weaken offline core behavior.

## 15. Data Persistence and State Management

Persist locally:

- Selected folder path/reference and any platform-specific access information supported by the chosen desktop implementation.
- Repeat/shuffle preferences, volume/mute preference, and user-facing UI preferences.
- Optional last queue/current track/position only after deciding privacy, restore, and stale-file behavior.
- A library index/cache only if it improves startup or browsing enough to justify invalidation and recovery complexity.

Keep preferences/session data distinct from file-embedded metadata. Treat the saved folder path as untrusted/stale on every startup; re-check access. A normal desktop path does not necessarily grant access after a folder is moved, unmounted, or permission-changed. Provide a migration/versioning plan for any persisted schema. Avoid storing duplicate copies of entire audio files.

The project currently has no selected persistence mechanism. Choose one based on the final JVM data model, migration needs, and acceptable corruption/rebuild behavior; do not add a database unless the index scale or query requirements justify it.

## 16. Error Handling and Recovery

| Condition | Required response |
|---|---|
| Folder selection cancelled | Stay on onboarding/settings without treating cancellation as an error or changing the existing folder. |
| Folder missing or inaccessible at launch | Explain the saved location is unavailable; offer retry and choose-folder actions; retain recoverable preferences. |
| Empty folder / no MP3 files | Distinguish “folder is empty” from “no supported MP3 files found”; show scan result and folder-change/rescan actions. |
| Unreadable nested directory or file | Continue other readable work where possible; report skipped item/count and allow inspection or retry. |
| Scan interrupted | Keep last valid index state, mark the scan incomplete, and do not infer deletions from incomplete traversal. |
| Corrupt/unsupported audio | Identify the track, stop/skip only with clear behavior, and leave its file unchanged. |
| Malformed/missing tags | Display sensible fallback values; keep the audio available if decodable. |
| Metadata/lyrics write failure | Preserve editor contents, report the underlying class of failure, do not update the library as if save succeeded, and allow retry. |
| Insufficient storage or replacement failure | Retain/recover the original where possible; report that the file may need inspection if recovery cannot be guaranteed. |
| Folder changed during playback | Handle the current file's actual availability; do not crash or silently switch to an unrelated track. |

Avoid broad exception catches that conceal failures. Translate expected filesystem/decoder/tag-writing exceptions into actionable UI states while retaining diagnostic context in local logs only if logging is later added and privacy rules are defined.

## 17. UI/UX and Material Design Guidelines

- Use Compose Multiplatform, already present, and confirm the requested “CMP” interpretation.
- Use the Material 3 component/design system only after confirming the selected dependency version is stable and compatible; current Material 3 is declared as `1.12.0-alpha03`.
- Establish semantic color tokens, typography, spacing, shapes, icon style, and focus/hover/pressed/disabled/error states before expanding screens.
- Keep playback controls available and visually coherent across library, details, and lyrics surfaces.
- Provide distinct loading, empty, error, confirmation, and success states. Success is shown only after an operation succeeds.
- Do not rely on color alone for playback, scan, error, or selection states.
- Use responsive desktop layouts and keyboard/focus behavior consistent with native desktop expectations. Verify each packaged OS rather than assuming identical behavior.

## 18. Accessibility and Usability

- Give controls accessible names and state descriptions (for example, “Pause”, “Repeat one”, and current seek position).
- Ensure all essential actions can be reached and operated using keyboard navigation; provide visible focus indication and logical tab order.
- Use readable text sizing, adequate contrast, and non-color-only state indicators.
- Ensure lyrics highlighting remains readable and does not flash or obscure text.
- Provide confirmation for irreversible/high-risk actions and a clear discard/cancel path for unsaved edits.
- Test with keyboard-only operation and at least one screen-reader/accessibility inspection on each supported desktop OS.

## 19. Performance and Scalability

- Perform scanning, tag parsing/writing, artwork decoding, and audio I/O off the UI thread.
- Load artwork on demand with bounded cache size; scale previews rather than retaining full-resolution images for every track.
- Avoid loading audio bytes or all metadata records into unbounded memory.
- Keep search/index results responsive for the target collection; benchmark against the proposed 10,000-track corpus and targets in Section 10.
- Ensure cancellation, navigation, and application shutdown do not leave active scans/writes or leak file handles.
- Measure cold scan, cached startup, memory use, artwork behavior, and search latency on reference systems before release; revise proposed targets based on evidence.

## 20. Security, Privacy, and Local File Safety

- Operate only on the chosen folder and files the user explicitly opens or selects; do not recursively modify files.
- Never alter audio/tag data during scanning or playback.
- Validate paths and file types, handle symbolic links consistently, and prevent a scan from escaping the selected root unless an explicit user decision permits it.
- Minimize permissions and avoid network access for core workflows.
- Avoid logging full file contents, lyrics, or unnecessary personal path data.
- Protect writes from partial replacement and preserve unrelated tag frames as specified in Section 12.
- Clearly tell users that metadata and lyric edits modify the original MP3 file.

## 21. Testing Strategy

### Unit tests

- Folder/reference validation and persisted preference restoration.
- File discovery, extension handling, duplicate prevention, scan reconciliation, and interrupted-scan semantics.
- Metadata fallbacks, field validation, tag version/encoding round trips, and preservation of unrelated frames.
- Unsynchronized lyric encoding/newline round trips and synchronized timestamp parsing, ordering, duplicates, and active-line selection.
- Queue, repeat, shuffle, next/previous, seek boundaries, and track-end transitions.
- Persistence serialization, migration, and stale-path handling.

### Integration and manual tests

- Run without network access through onboarding, scan, browse, playback, editing, restart, and settings.
- Exercise empty, large, nested (once policy is decided), inaccessible, removable, and changed folders.
- Exercise valid, missing-tag, malformed-tag, read-only, corrupted, long-duration, and varied ID3v2.3/v2.4 MP3 samples. Use legally obtained test files.
- Verify saved tags in an independent tag-inspection/player tool and confirm audio content remains playable.
- Verify synchronized lyrics against playback time and ensure edits round-trip.
- Package and smoke-test DMG, MSI, and DEB artifacts on their respective supported operating systems.
- Check keyboard navigation, accessibility names/states, window resizing, high-DPI artwork, and error messaging.

### Test data safety

Perform destructive metadata-write tests only on disposable copies of fixtures. Include before/after checks for audio payload and unrelated frames; do not use a user's library as test data.

## 22. Acceptance Criteria

The initial release is acceptable when all **Must** requirements in Section 7 are met and the following observable checks pass:

1. A clean installation opens onboarding. Cancelling folder selection leaves the user on onboarding and does not invent an empty selected library.
2. Selecting a readable folder starts a visible scan; discovered `.mp3` files appear once each, even after repeated full rescans.
3. Restarting the application restores the selected folder when still accessible. Removing/renaming/unmounting it yields a recovery state, not an unhandled failure or repeated modal loop.
4. Empty, no-supported-file, partial-inaccessibility, and interrupted scans are distinguishable and do not cause incomplete scans to delete unseen entries.
5. Every essential playback control and queue transition can be exercised; unplayable files produce a clear, recoverable outcome.
6. Playback continues across internal screen navigation. Repeat-off, repeat-one, repeat-all, and shuffle behavior matches the documented rules.
7. A supported tag edit persists to the original MP3, appears after reload/rescan, leaves audio playable, and preserves unrelated supported/unknown frames in the fixture suite. Unsupported destructive cases do not silently write.
8. Unsynchronized lyrics round-trip multiline Unicode text. Synchronized lyric highlighting tracks playback within the specified update target when the format is supported.
9. Core flows complete with networking disabled. No core workflow sends music, lyrics, metadata, or user paths to a remote service.
10. Keyboard navigation and accessibility states work for essential library, player, editor, and settings controls.
11. Proposed performance targets are measured on documented reference hardware and either met or explicitly revised before release.

## 23. Implementation Phases and Priorities

| Phase | Deliverable | Depends on | Completion criteria |
|---|---|---|---|
| 1. Repository analysis and decisions | Confirm desktop support matrix, “CMP” meaning, scan scope, playback/session behavior, and library choices. | None | Open decisions that block design are resolved or explicitly deferred; compatibility/license checks are recorded. |
| 2. Application shell and design system | Replace starter greeting screen with navigation/layout, Material 3 policy, player surface, and state components. | 1 | Screen shell works at target window sizes; keyboard/focus and empty/error patterns are established. |
| 3. Folder selection and persistence | Onboarding, chooser, saved location, restore, change-folder and recovery flows. | 1, 2 | FR-001–004 and folder acceptance checks pass across supported OSes. |
| 4. Discovery and library | Scanner, metadata read model, index/cache decision, browse/search/sort, progress and partial failures. | 3 | FR-005–008 pass, repeated scans do not duplicate entries, and scan recovery is tested. |
| 5. Playback engine and controls | Decoder/player integration, queue, transport, seek, volume, repeat/shuffle, player UI. | 4 | FR-009–012 pass with representative valid/unplayable fixtures. |
| 6. Metadata reading/editing | Select and integrate JVM tag library; implement safe editing and round-trip verification. | 4, 1 | FR-013–014 pass without audio/unrelated-frame loss in fixture tests. |
| 7. Lyrics | Embedded USLT/SYLT reading, unsynchronized editing, synchronized display and optional safe edit. | 6, 5 | FR-015–017 supported behavior and timestamp tests pass; limitations are surfaced. |
| 8. Recovery, performance, accessibility | Error mapping, storage/write recovery, profiling, accessibility, OS-specific verification. | 3–7 | Recovery matrix and target performance/accessibility checks are complete. |
| 9. Automated testing and release preparation | Full regression, offline and packaging checks, release notes and support limitations. | 8 | Acceptance criteria pass on packaged target platforms; no unresolved Must requirement remains. |

## 24. Future Enhancements

Potential future work, only after the offline MP3 release is stable:

- Additional local audio formats after decoder and metadata compatibility review.
- Playlists, favorites, ratings, and richer library organization.
- Sidecar `.lrc` import/export with explicit conflict and precedence rules.
- More complete global media-key, tray, and operating-system integration after platform verification.
- Optional online lyric lookup or synchronization only with explicit opt-in, privacy review, and a robust offline fallback.
- Library backup/export and user-controlled migration tools.

## 25. Open Questions and Decisions Requiring Confirmation

**1. Does "CMP" mean Compose Multiplatform?**
Almost certainly yes. The repo README says "Compose Multiplatform applications", and the build plugins are `composeMultiplatform` and `composeCompiler`. Your prompt told the spec author not to assume, so the spec should state it as "confirmed from repository evidence" and ask you to confirm. Please reply "yes" if that's what you meant.

**2. Nested subfolders and symbolic links**
- **Subfolders:** Include them, recursively. Music collections are usually organized as `Artist/Album/track.mp3`, so a flat scan would find almost nothing for most users. Add an optional setting for maximum depth only if testing shows a need.
- **Symlinks to files:** Follow them, but index each real file once, using its canonical path to avoid duplicates.
- **Symlinks to directories:** Skip them by default. This prevents infinite loops (a link pointing back to a parent folder) and scans escaping the chosen folder. Report skipped links in the scan summary so the user knows why files might be missing.
- **Inaccessible subfolders:** Skip them, continue the scan, and list them in the summary.

If you want symlinked directories followed, the scanner would need loop detection (tracking visited canonical paths). I'd make that "Could have".

**3. OS versions for the DMG, MSI and DEB packages**
I can't answer this from the repo. The page I read didn't show `desktopApp/build.gradle.kts`, so I haven't seen the `nativeDistributions` block, and I don't want to invent version numbers. Here is how to find out:
- Open the `compose.desktop { application { nativeDistributions { … } } }` block in `desktopApp/build.gradle.kts`. It lists `targetFormats` (Dmg, Msi, Deb) and metadata such as package name and version.
- The minimum OS versions come from your JDK and Compose Multiplatform versions, not from that block. Check the release notes for the Compose Multiplatform and JDK versions in `libs.versions.toml`.
- Be aware that `jpackage` builds only for the OS it runs on. A DMG needs macOS, an MSI needs Windows (with WiX), and a DEB needs Linux. Building all three needs three machines or CI runners.

Until you've confirmed the details, the spec should list the support matrix as "to be verified in Phase 1", and the acceptance tests should name the exact OS versions you actually tested on, not a claimed minimum.

**4. Folder chooser and persistent path**
- **Chooser:** For v1, use the AWT/Swing option that ships with the JDK, so there's no extra dependency to review for licensing or offline behavior. `FileDialog` with the macOS directory-selection property gives a native look on macOS, and `JFileChooser` in directories-only mode works on Windows and Linux. A library such as FileKit gives a more native feel everywhere, but it's one more dependency. Evaluate it only if the stock choosers look wrong in testing.
- **Persistence:** Desktop JVM has no sandbox permission grant to persist (unlike Android URIs). Store the absolute folder path in a settings file in the OS app-data directory, then on every launch check that the folder exists, is a directory, and is readable.
- **Failure handling:** If any check fails, show the "folder unavailable" screen with "Choose another folder" and "Retry" actions, and don't discard the old path until a new one is saved. That covers unplugged drives and renamed folders.
- **Caveat:** If you later package for the Mac App Store, App Sandbox would require security-scoped bookmarks. DMG distribution doesn't, so this is not a v1 concern, but it should be listed as a future risk.
  **5. Which JVM audio decoder/player library meets MP3, seek, threading, licensing, packaging, and offline requirements?**
  I can't answer this definitively from the repo, because no audio dependency exists yet and library behavior depends on versions. Run a time-boxed spike on two or three candidates (JLayer with an MP3 SPI, JavaFX Media, vlcj), judged on:
- **Seek accuracy:** test on VBR files, which are the usual failure case.
- **Threading:** playback must run on a background thread without blocking the UI.
- **Licensing:** the license must allow distribution.
- **Packaging:** it must bundle without a separate install. vlcj needs VLC installed, so it likely fails the offline and packaging requirement.

My default is to avoid vlcj and choose between JLayer and JavaFX Media based on the spike. The spike results, not my recollection of these libraries, should go into the spec.

**6. Which JVM ID3 library has acceptable ID3v2.3/v2.4, artwork, `USLT`/`SYLT`, encoding, and unknown-frame preservation behavior?**
Don't decide from feature lists. Evaluate JAudioTagger and mp3agic with a fixture-based round-trip test:
- Write a tag, re-read it, and confirm that unknown frames, artwork, `USLT`, `SYLT` and text encodings survive unchanged.
- Check whether the library writes v2.3 or v2.4.

My provisional default is JAudioTagger, because it covers more frame types. That is an assumption until the round-trip tests pass.

**7. Should synchronized lyrics be editable in the first release or read-only until safe `SYLT` writing is verified?**
Make them read-only in the first release, with display and highlighting. Enable `SYLT` editing only once the round-trip tests from item 6 pass. Treat it as "Should have, gated on tests".

**8. Are `.lrc` sidecars in scope? If yes, what naming, precedence, write, and conflict rules should apply?**
Support reading them in v1, but not writing.
- **Naming:** use the same base filename as the MP3 (`song.mp3` → `song.lrc`).
- **Precedence:** embedded `SYLT`, then `.lrc`, then `USLT`.
- **Conflicts:** show a visible indicator of which source is displayed, plus a manual override.
- **Writing:** `.lrc` writing is "Could have".

**9. What is the queue policy when a track is selected, the folder changes, or the current file becomes unavailable?**
- **Track selected:** it replaces the queue with the current list (library view or search result), starting at that track.
- **Folder changed:** it stops playback and clears the queue, after a confirmation.
- **File unavailable:** show an error, mark the track unavailable, and skip to the next playable track. Stop if none are playable.

**10. Should a previous session reopen paused, auto-resume, or restore only the selected track? Should exact playback position persist?**
Restore the last track and exact position on launch, but start paused. Auto-resuming sound on launch surprises people. Save the position every few seconds, and also on pause and exit.

**11. Should playback continue when the window is closed/minimized, and are global media keys required?**
- **Close:** closing the window quits the app.
- **Minimize:** minimizing keeps playback running.
- **System tray:** skip it in v1.
- **Media keys:** global media keys are "Could have", since they need native hooks that differ by OS.

**12. Should repeat/shuffle/volume settings and the last queue persist? What should happen to a queue on folder replacement?**
Persist repeat, shuffle and volume. Persist the queue as a list of paths relative to the music root. On folder replacement, clear the queue but keep repeat, shuffle and volume.

**13. Is a local database justified, or is a simpler rebuildable index sufficient for the expected collection size?**
No database for v1, since 10,000 tracks fit comfortably in memory. Use a rebuildable JSON index keyed by path, file size and modified time, which can be deleted safely at any time. Move to SQLite only if the measured scan or search targets in item 16 fail.

**14. Should edits create backups? If so, where are backups stored, how are they named, and how are they retained or removed?**
Make the safe write the default:
- Write to a temporary file in the same directory.
- Flush and verify it.
- Atomically replace the original.
- Delete the temp file.

Don't keep permanent backups by default, because they clutter the user's music folder. As an optional "keep one backup" setting, off by default:
- **Location:** the app-data folder.
- **Naming:** `<hash>-<timestamp>.mp3`.
- **Retention:** one backup per file.

**15. Which Material 3 dependency/version policy is acceptable given the currently declared alpha artifact?**
I haven't seen `gradle/libs.versions.toml`, so I can't confirm which artifact is declared. Check it first. A common reason for an alpha is the newer Material 3 Expressive components. My suggested policy:
- Use stable versions by default.
- Allow an alpha only if a required component isn't stable yet.
- If you do use one, pin the exact version, never a dynamic one, and note why in the version catalog.

**16. Are the proposed performance targets and 10,000-track target collection appropriate for intended users?**
These are proposals to validate on baseline hardware, not measured facts:

| Metric | Proposed target (10,000 tracks) |
|---|---|
| Launch to usable UI with a warm index | ≤ 2 s |
| First results visible during a cold scan | ≤ 5 s |
| Full metadata scan (SSD) | ≤ 60 s |
| Search results after a keystroke | ≤ 100 ms |
| Seek response | ≤ 250 ms |
| Memory use | ≤ 500 MB |

10,000 tracks is reasonable for most personal libraries, but some people have 30,000 or more. Keep 50,000 as a stress-test size.

Please confirm or change the defaults in 7, 10, 11 and 14 in particular, since those affect user-visible behavior most. Once you do, I can write the full `PROJECT_SPECIFICATION.md` with these decisions and the remaining open questions marked.
Please confirm #1 and tell me whether you want symlinked directories followed. If so, I'll mark it as a decision and proceed to write the full specification.


10,000 tracks is reasonable for most personal libraries, but some people have 30,000 or more. Keep 50,000 as a stress-test size.

Do you want me to write the full PROJECT_SPECIFICATION.md with these defaults and the open questions marked as such? If you disagree with any default, tell me which and I’ll use your answer.

These decisions should be resolved before the affected implementation phase begins; unresolved optional features must not block the dependable offline playback core.
