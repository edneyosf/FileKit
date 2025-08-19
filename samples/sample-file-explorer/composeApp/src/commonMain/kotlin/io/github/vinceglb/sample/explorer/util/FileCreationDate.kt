package io.github.vinceglb.sample.explorer.util

import io.github.vinceglb.filekit.PlatformFile
import kotlin.time.Instant

expect fun PlatformFile.createdAt(): Instant?

expect fun PlatformFile.lastModified(): Instant
