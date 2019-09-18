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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.kowts.utils.functions.F

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-08-23 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 06 - master - handling duplicate bitmap
 */
object ImageHandler {

    // get bitmap
    fun getBitmap(bitmap: Bitmap?, context: Context, callback: (Bitmap?) -> Unit) {

        GlobalScope.launch {
            val future = Glide.with(context)
                    .asBitmap()
                    .load("https://source.unsplash.com/random")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .submit()

            try {
                val bitmapN = future.get()
                F.compareBitmaps(bitmap, bitmapN) {
                    if (it)
                        getBitmap(bitmap, context, callback)
                    else
                        (context as AppCompatActivity).runOnUiThread { callback(bitmapN) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}