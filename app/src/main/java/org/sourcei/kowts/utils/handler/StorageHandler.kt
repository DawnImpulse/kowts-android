/**
 * ISC License
 *
 * Copyright 2018-2019, Saksham (DawnImpulse)
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 **/
package org.sourcei.kowts.utils.handler

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import com.crashlytics.android.Crashlytics
import org.sourcei.kowts.utils.functions.logd
import java.io.File
import java.io.FileOutputStream

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-11 by Saksham
 * @note Updates :
 */
object StorageHandler {

    // store bitmap in file
    fun storeBitmapInFile(context:Context,bitmap: Bitmap, file: File) {

        try {
            logd(file.path)
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut)
            fOut.flush()
            fOut.close()

            // make available for media scanner
            MediaScannerConnection.scanFile(context, arrayOf(file.toString()), arrayOf("image/jpeg"), null)
        } catch (e: Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
        }
    }
}