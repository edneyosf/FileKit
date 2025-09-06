package io.github.vinceglb.filekit

import io.github.vinceglb.filekit.exceptions.FileKitException
import io.github.vinceglb.filekit.utils.calculateNewDimensions
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.*

public actual val FileKit.projectDir: PlatformFile
    get() = PlatformFile(nsUrl = NSBundle.mainBundle.bundleURL)

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public actual suspend fun FileKit.saveImageToGallery(
    bytes: ByteArray,
    filename: String
): Unit = withContext(Dispatchers.IO) {
    val nsData = bytes.usePinned {
        NSData.create(
            bytes = it.addressOf(0),
            length = bytes.size.toULong()
        )
    }
    val uiImage = UIImage(nsData)
    UIImageWriteToSavedPhotosAlbum(uiImage, null, null, null)
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun compress(
    nsData: NSData,
    quality: Int,
    maxWidth: Int?,
    maxHeight: Int?,
    imageFormat: ImageFormat,
): NSData {
    val originalImage = UIImage(data = nsData)
    val originalWidth = originalImage.size.useContents { width }.toInt()
    val originalHeight = originalImage.size.useContents { height }.toInt()

    val (newWidth, newHeight) = calculateNewDimensions(
        originalWidth,
        originalHeight,
        maxWidth,
        maxHeight
    )

    val resizedImage = originalImage.scaleToSize(newWidth, newHeight)
        ?: throw FileKitException("Failed to resize image")

    return when (imageFormat) {
        ImageFormat.JPEG -> UIImageJPEGRepresentation(resizedImage, quality / 100.0)
        ImageFormat.PNG -> UIImagePNGRepresentation(resizedImage)
    } ?: throw FileKitException("Failed to compress image")
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.scaleToSize(newWidth: Int, newHeight: Int): UIImage? {
    val size = CGSizeMake(newWidth.toDouble(), newHeight.toDouble())
    UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
    this.drawInRect(CGRectMake(0.0, 0.0, newWidth.toDouble(), newHeight.toDouble()))
    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return resizedImage
}
