package io.github.vinceglb.filekit.dialogs.deprecated

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write

@Deprecated(message = "Use the function without the bytes parameter. More info in the migration guide: https://filekit.mintlify.app/migrate-to-v0.10")
public actual suspend fun FileKit.openFileSaver(
    bytes: ByteArray?,
    suggestedName: String,
    extension: String?,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings
): PlatformFile? {
    val file = FileKit.openFileSaver(
        suggestedName = suggestedName,
        extension = extension,
        directory = directory,
        dialogSettings = dialogSettings,
    )

    if (file != null && bytes != null) {
        file.write(bytes)
    }

    return file
}
