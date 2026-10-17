# Prompt: Generate a Detailed Project Specification for Webdude Music Player

Act as a senior software architect, product analyst, and technical documentation specialist. Your task is to transform my initial project instructions into a comprehensive, professional Markdown (`.md`) document that serves as the **product requirements and technical planning specification** for my music player application.

## 1. Project Context

The project is named **Webdude Music Player**.

Repository: https://github.com/SoumabhaSaha15/WebdudeMusicPlayer

Before writing the document, inspect the existing repository to understand its current technology stack, application structure, dependencies, implemented features, and coding conventions. Base implementation-specific recommendations on the actual repository rather than assumptions.

If repository access is unavailable, clearly state that limitation and work only with the requirements below.

## 2. Core Objective

The application will be a fully offline music player focused initially on MP3 audio files. It should provide a clean, modern user experience, reliable local music library management, complete playback controls, and the ability to edit audio metadata and lyrics.

The generated document must be detailed enough to guide future development by an AI coding assistant or a human developer.

## 3. Initial Requirements

### A. User Interface and Design

- Use the latest Material Design version supported by the project's existing framework and the relevant component library referred to as `CMP`.
- Investigate what `CMP` means in the repository context. Do not assume a specific library if the acronym is unclear.
- Follow a consistent design system covering colors, typography, spacing, icons, component states, and layout.
- Ensure the interface is responsive and usable at the screen sizes supported by the project.
- Include suitable loading, empty, error, confirmation, and success states.
- Prioritize usability, accessibility, and intuitive navigation.

### B. Offline-First Architecture

- The application must operate without an internet connection after installation or initial setup, as applicable to its platform.
- Music playback, library browsing, metadata editing, lyrics editing, and settings must work offline.
- Local music files must remain on the user's device.
- Do not introduce mandatory cloud services, online accounts, remote APIs, or telemetry that requires connectivity.
- Clearly distinguish between features that must work offline and any optional future feature that might require internet access.
- Consider data persistence, filesystem permissions, file access failures, and application restarts.

### C. Initial Folder Selection

- On the first launch, display a welcome or onboarding page asking the user to select a music folder.
- Use the appropriate native or framework-supported folder-selection mechanism for the project's platform.
- After selection, persist the selected folder reference or permission using the platform's supported approach.
- On subsequent launches, attempt to reopen the previously selected folder automatically.
- Avoid asking the user to select the folder again unless necessary.
- If the folder is deleted, moved, becomes inaccessible, or its permission is revoked, detect the failure and guide the user to select a valid folder again.
- Provide appropriate feedback when a folder cannot be accessed.
- Allow the user to change the selected music folder later through settings.
- Define behavior for empty folders, folders containing no supported audio files, inaccessible subfolders, and interrupted folder scans.

### D. Music Library Management

- Discover MP3 files in the selected folder.
- Determine whether scanning should include nested subfolders. If this is not already established by the repository, identify it as a decision requiring confirmation.
- Extract available metadata, including title, artist, album, album artist, track number, disc number, genre, year, duration, and embedded artwork where available.
- Handle missing, malformed, incomplete, or inconsistent metadata gracefully.
- Display the music library with useful browsing and sorting options.
- Support searching and filtering the local library.
- Define how library changes are detected when files are added, removed, renamed, or modified.
- Avoid duplicating tracks during rescans.
- Distinguish between metadata read from files and any application-maintained information.
- Consider large music collections and the performance implications of scanning and metadata extraction.

Do not assume that all MP3 files contain complete or valid metadata.

### E. Audio Playback

Specify the expected behavior for a complete music player, including:

- Play and pause.
- Next and previous track.
- Seek forward and backward.
- A seek bar showing playback progress and total duration.
- Volume control and mute.
- Repeat modes, including repeat-one and repeat-all.
- Shuffle playback.
- Current track information and album artwork.
- A playback queue or playlist.
- Track transitions and end-of-track behavior.
- Handling unavailable, corrupted, or unsupported audio files.
- Playback state when navigating between application screens.
- Restoration of the last playback position or session, if supported by the selected architecture.
- Keyboard shortcuts or hardware media controls where supported by the platform.
- Appropriate handling of audio focus, interruptions, and background playback where applicable.

Separate essential playback features from optional enhancements. Do not claim that platform-specific capabilities are supported until the technology stack has been verified.

### F. ID3 Metadata Editing

The application must allow users to edit supported ID3 metadata for MP3 files.

Document requirements for editing fields such as:

- Title.
- Artist.
- Album.
- Album artist.
- Genre.
- Year or recording date.
- Track number and total tracks.
- Disc number and total discs.
- Composer, where supported.
- Embedded album artwork, where supported.

Include requirements for:

- Reading existing tags.
- Editing and validating values.
- Saving changes back to the original file.
- Refreshing the displayed metadata after a successful save.
- Handling read-only files, permission failures, malformed tags, and insufficient storage.
- Preserving unrelated tags and audio content when modifying metadata.
- Confirming destructive or potentially irreversible operations where appropriate.
- Preventing data loss through safe write operations, backups where justified, and error recovery.

Explain the relevant ID3 version and compatibility considerations based on the chosen library and implementation. Do not prescribe an unsupported library or make assumptions about its capabilities.

### G. Lyrics Support

Support both synchronized and unsynchronized lyrics.

**Unsynchronized lyrics:**
- Display ordinary multiline lyrics.
- Allow the user to edit and save lyrics.
- Preserve line breaks and text encoding correctly.

**Synchronized lyrics:**
- Support timestamped lyric lines.
- Display the appropriate lyric line in relation to the current playback position.
- Update the highlighted line during playback.
- Allow editing of timestamps and lyric text where feasible.
- Define behavior when timestamps are missing, invalid, duplicated, or out of order.

Investigate the appropriate metadata frames or storage formats for lyrics, including relevant ID3 frames and common synchronized lyric formats where applicable.

Explain how embedded lyrics will be read and written, how conflicting lyric sources will be handled, and how changes will be saved without unintentionally removing unrelated metadata.

The initial implementation should remain fully offline. Do not require online lyric searches or lyric-provider APIs.

### H. Persistence and Application Settings

Define which application preferences and playback information should survive application restarts.

Consider:

- The selected music folder and its access permissions.
- Playback preferences.
- Shuffle and repeat settings.
- Volume preference.
- Last played track and playback position, if appropriate.
- User interface preferences.
- Library scan state or cache, if required.

Distinguish application settings from metadata stored directly in music files. Identify an appropriate persistence mechanism only after verifying the project's framework and platform.

## 4. Required Markdown Document Structure

Generate a polished Markdown document containing the following sections:

1. Project Overview
2. Project Goals
3. Scope and Non-Goals
4. Existing Repository and Technology Stack
5. Product Principles
6. Target Users and Main Use Cases
7. Functional Requirements
8. Detailed User Journeys
9. Application Navigation and Screen Specifications
10. Music Library and File Discovery
11. Audio Playback Requirements
12. ID3 Metadata Editing
13. Synchronized and Unsynchronized Lyrics
14. Offline-First Architecture
15. Data Persistence and State Management
16. Error Handling and Recovery
17. UI/UX and Material Design Guidelines
18. Accessibility and Usability
19. Performance and Scalability
20. Security, Privacy, and Local File Safety
21. Testing Strategy
22. Acceptance Criteria
23. Implementation Phases and Priorities
24. Future Enhancements
25. Open Questions and Decisions Requiring Confirmation

Adapt or remove sections only when there is a clear reason.

## 5. Technical Accuracy Requirements

- Use the repository as the source of truth for the existing stack and implementation.
- Clearly distinguish existing functionality, planned functionality, and proposed improvements.
- Do not invent completed features, dependencies, APIs, file paths, classes, or architectural decisions.
- Verify framework and library capabilities before recommending them.
- Where the framework or platform is unknown, explain the requirement in platform-neutral terms.
- Identify technical risks, trade-offs, and dependencies.
- Flag ambiguous requirements as open questions rather than silently making consequential decisions.
- Do not introduce unnecessary complexity or overengineer the initial release.
- Keep the first release focused on a reliable offline MP3 player.
- Treat support for additional audio formats as a future extension unless the repository already establishes otherwise.

## 6. Requirements and Acceptance Criteria

Write requirements in a testable form. Give important requirements unique identifiers, such as:

- `FR-001`: First-launch folder selection.
- `FR-002`: Persistence of the selected folder.
- `FR-003`: Detection of an unavailable music folder.
- `FR-004`: Discovery of MP3 files.
- `FR-005`: Basic playback controls.
- `FR-006`: ID3 metadata editing.
- `FR-007`: Unsynchronized lyrics editing.
- `FR-008`: Synchronized lyrics playback.

Expand this list as necessary.

For each significant feature, document:

- Requirement ID.
- Description.
- Expected behavior.
- Preconditions.
- Main workflow.
- Failure scenarios.
- Acceptance criteria.
- Priority: Must have, Should have, Could have, or Won't have in the initial release.

Use measurable acceptance criteria wherever possible. For example, verify that the previously selected folder is reused after restarting the application when the folder and its permissions remain valid.

## 7. Implementation Planning

Break implementation into practical phases, such as:

1. Repository analysis and architectural decisions.
2. Application shell, navigation, and design system.
3. Folder selection and persistent access.
4. Music discovery and library presentation.
5. Playback engine and player controls.
6. Metadata reading and editing.
7. Lyrics reading, editing, and synchronization.
8. Error handling, performance, and accessibility.
9. Automated testing and release preparation.

Adjust the phases to fit the actual project structure. Identify dependencies between phases and define clear completion criteria.

Do not begin implementing code. This task is documentation and planning only.

## 8. Writing Style and Output

- Output the complete document as valid Markdown.
- Use clear headings, concise technical explanations, tables where useful, and checklists for implementation tasks.
- Use consistent terminology throughout.
- Avoid vague statements such as "the application should be fast" without explaining what good performance means.
- Include code examples or diagrams only when they materially clarify an architectural requirement.
- Use Mermaid diagrams for workflows when they improve understanding.
- Do not fill the document with generic explanations unrelated to this project.
- Avoid repeating the same requirement in multiple sections unless a cross-reference is useful.
- Clearly mark assumptions and unresolved decisions.
- Do not fabricate external references or citations.

## 9. Final Validation

Before delivering the Markdown document, verify that:

- Every original requirement is represented.
- First-launch folder selection and subsequent folder restoration are clearly specified.
- Missing, deleted, or inaccessible folders are handled.
- Core playback functionality is covered.
- Both synchronized and unsynchronized lyrics are addressed.
- ID3 edits account for file integrity and metadata preservation.
- Offline operation is treated as a core requirement.
- Requirements are testable and prioritized.
- Repository facts are not confused with proposed features.
- Open questions are explicitly listed.
- The document is internally consistent and suitable for use as the project's ongoing development specification.

## Deliverable

Produce the complete specification as `PROJECT_SPECIFICATION.md`.

If you have access to the project workspace and file-writing capabilities, create the file there. Otherwise, return the entire Markdown content in your response so it can be saved as `PROJECT_SPECIFICATION.md`.

Remember: document and plan the application, but do not implement the application in this task.