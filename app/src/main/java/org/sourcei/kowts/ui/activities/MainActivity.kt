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
package org.sourcei.kowts.ui.activities

import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.Permissions
import org.sourcei.kowts.R
import org.sourcei.kowts.utils.functions.*
import org.sourcei.kowts.utils.handler.ImageHandler
import org.sourcei.kowts.utils.handler.StorageHandler
import org.sourcei.kowts.utils.pojo.ObjectQuote
import org.sourcei.kowts.utils.reusables.ALIGN_LEFT
import org.sourcei.kowts.utils.reusables.Angles
import java.io.File

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-08-20 by Saksham
 * @note Updates :
 *  Saksham - 2019 08 27 - master - random alignment
 *  Saksham - 2019 09 05 - master - handling button clicks
 *  Saksham - 2019 09 07 - master - multiple quote handling
 *  Saksham - 2019 09 12 - master - quote additional properties
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    var bitmap: Bitmap? = null
    lateinit var quoteObject: ObjectQuote

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDimensions(F.displayDimensions(this))
        getRandomQuote()

        refresh.setOnClickListener(this)
        download.setOnClickListener(this)
    }

    // button click handling
    override fun onClick(v: View) {
        when (v.id) {

            refresh.id -> getRandomQuote()

            download.id -> {
                Permissions.askWriteExternalStoragePermission(this) { e, r ->
                    e?.let {

                    }
                    r?.let {
                        GlobalScope.launch {
                            val b = F.generateBitmap(this@MainActivity, quoteObject)
                            val file = File(Environment.getExternalStorageDirectory().path, "abcdefg.jpg")
                            StorageHandler.storeBitmapInFile(this@MainActivity, b, file)
                            runOnUiThread {
                                toast(file.toString())
                            }
                        }
                    }
                }
            }

        }
    }

    // get random quotes
    private fun getRandomQuote() {

        progress.show()
        card.gone()

        F.getQuote(this) { pojo ->
            if (pojo != null) {

                ImageHandler.getBitmap(bitmap, this) {
                    if (it != null) {

                        // get random angles & gradient colors
                        val colors = F.randomGradient().toIntArray()
                        val colorsAuthor = F.randomGradient().toIntArray()
                        val angle = Angles.random().toFloat()

                        // create quote object
                        quoteObject = ObjectQuote(pojo.quote, colors, angle, pojo.author, colorsAuthor, it, ALIGN_LEFT, ALIGN_LEFT)

                        // set details
                        bitmap = it
                        background.setImageBitmap(it)
                        setBackground()

                        gradient.setGradient(colors, 0, angle)
                        blurMask.setGradient(colors, 0, angle)
                        authorLayout.setGradient(colorsAuthor, 16)

                        quote.text = pojo.quote
                        author.text = pojo.author

                        card.show()
                    } else
                        toast("error fetching quote image")

                    progress.gone()
                }
            } else {
                toast("error fetching quote")
                progress.gone()
            }
        }
    }

    // set card & text dimensions
    private fun setDimensions(point: Point) {

        // set dimensions for card
        val x = point.x - F.dpToPx(32, this)
        val y = x

        val params = RelativeLayout.LayoutParams(x, y)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.topMargin = (point.y - y) / 4
        card.layoutParams = params


        // set dimensions for quote & author layout
        val margin = F.dpToPx(16, this)

        // original params
        val paramsT = quote.layoutParams // original params for quote
        val paramsA = authorCard.layoutParams // original params for author

        // new params
        val paramsNQ = RelativeLayout.LayoutParams(paramsT.width, 3 * y / 4)
        val paramsNA = RelativeLayout.LayoutParams(paramsA.width, paramsA.height)

        paramsNQ.setMargins(margin)
        paramsNA.setMargins(margin, 0, margin, margin)
        paramsNA.addRule(RelativeLayout.BELOW, R.id.quote)

        // set alignment
        authorCard.layoutParams = paramsNA
        quote.layoutParams = paramsNQ
    }

    // set background
    private fun setBackground() {
        Blurry.with(this)
                .async()
                .sampling(1)
                .from(bitmap)
                .into(blurBg)
    }

    // change quote alignment
    private fun changeAlignment(align: Int) {
        quote.gravity = when (align) {
            0 -> Gravity.LEFT
            1 -> Gravity.CENTER
            else -> Gravity.RIGHT
        }
        quoteObject.quoteAlign = align
    }

    // change author alignment
    private fun changeAuthorAlignment(align: Int) {
        val params = authorCard.layoutParams as RelativeLayout.LayoutParams
        params.addRule(
                when (align) {
                    0 -> RelativeLayout.ALIGN_PARENT_LEFT
                    1 -> RelativeLayout.CENTER_HORIZONTAL
                    else -> RelativeLayout.ALIGN_PARENT_RIGHT
                }
        )
        authorCard.layoutParams = params
        quoteObject.authorAlign = align
    }

    // change gradient angle
    private fun gradientAngle(angle: Float) {
        gradient.setGradient(quoteObject.gradient, 0,angle)
        blurMask.setGradient(quoteObject.gradient, 0,angle)
        quoteObject.angle = angle
    }

    // change gradient design
    private fun changeGradient() {
        val colors = F.randomGradient().toIntArray()
        val angle = (0..180).random().toFloat()
        gradient.setGradient(colors, 0,angle)
        blurMask.setGradient(colors, 0,angle)

        quoteObject.gradient = colors
        quoteObject.angle = angle
    }

    // change gradient author
    private fun changeGradientAuthor() {
        val colors = F.randomGradient().toIntArray()
        authorLayout.setGradient(colors)
        quoteObject.authorGradient = colors
    }

    // change image
    private fun changeImage() {
        progressImage.show()
        ImageHandler.getBitmap(bitmap, this) {
            if (it != null) {
                bitmap = it
                quoteObject.image = it
                background.setImageBitmap(it)
                setBackground()
            } else
                toast("error fetching quote image")

            progressImage.gone()
        }
    }
}