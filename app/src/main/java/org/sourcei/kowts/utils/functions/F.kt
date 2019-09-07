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
package org.sourcei.kowts.utils.functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.kowts.network.Model
import org.sourcei.kowts.ui.pojo.PojoQuotes
import org.sourcei.kowts.utils.reusables.Paper
import org.sourcei.kowts.utils.reusables.QUOTES

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-08-20 by Saksham
 * @note Updates :
 * Saksham - 2019 09 06 - master - compare bitmap
 * Saksham - 2019 09 07 - master - handing of multiple quotes
 */
object F {

    // Generating random color
    private fun randomColor(): String {
        val chars =
                listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var color = "#"
        for (i in 1..6) {
            color += chars[Math.floor(Math.random() * chars.size).toInt()]
        }
        return color
    }

    // Generate random gradient
    fun randomGradient(): List<Int> {
        val count = 2
        val angle = (0..180).random()
        val colors = mutableListOf<Int>()

        for (i in 1..count) {
            colors.add(randomColor().toColorInt())
        }


        return colors

    }

    // convert dp - px
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    // get display height
    fun displayDimensions(context: Context): Point {
        val point = Point()
        val mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = mWindowManager.defaultDisplay
        display.getSize(point) //The point now has display dimens
        return point
    }

    // verify two bitmaps
    fun compareBitmaps(b1: Bitmap?, b2: Bitmap?, callback: (Boolean) -> Unit) {

        if (b1 == null || b2 == null) {
            callback(false)
        } else
            GlobalScope.launch {
                try {
                    callback(b1.sameAs(b2)) // callback with compare
                } catch (e: Exception) {
                    //Crashlytics.logException(e)
                    e.printStackTrace()
                    callback(false)
                }
            }
    }

    // get quote
    fun getQuote(activity: AppCompatActivity, callback: (PojoQuotes?) -> Unit) {

        val quotes: MutableList<PojoQuotes>? = Paper.read(QUOTES, null)

        // quote available in prefs
        if (quotes != null) {
            callback(quotes[0])

            if (quotes.size != 1)
                Paper.write(QUOTES, quotes.subList(1, quotes.size))
            else
                Paper.delete(QUOTES)

        } else {
            // fetch quote from server
            Model(activity).getRandomQuote { e, r ->
                e?.let {
                    loge(e)
                    callback(null)
                }
                r?.let {
                    callback(it[0])
                    Paper.write(QUOTES, it.subList(1, it.size))
                }
            }
        }
    }
}