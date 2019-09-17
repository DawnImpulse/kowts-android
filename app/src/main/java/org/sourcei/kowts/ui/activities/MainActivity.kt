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
 *  Saksham - 2019 09 13 - master - quote & author alignment
 *  Saksham - 2019 09 14 - master - options handling
 *  Saksham - 2019 09 17 - master - long click information
 */
class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {
    var bitmap: Bitmap? = null
    var loading = false
    lateinit var quoteObject: ObjectQuote

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDimensions(F.displayDimensions(this))
        getRandomQuote()

        // single click
        refresh.setOnClickListener(this)
        download.setOnClickListener(this)
        edit.setOnClickListener(this)
        quoteGradient.setOnClickListener(this)
        authorGradient.setOnClickListener(this)
        changeImage.setOnClickListener(this)
        quoteAlign.setOnClickListener(this)
        authorAlign.setOnClickListener(this)
        angle.setOnClickListener(this)

        // long click
        refresh.setOnLongClickListener(this)
        download.setOnLongClickListener(this)
        edit.setOnLongClickListener(this)
        quoteGradient.setOnLongClickListener(this)
        authorGradient.setOnLongClickListener(this)
        changeImage.setOnLongClickListener(this)
        quoteAlign.setOnLongClickListener(this)
        authorAlign.setOnLongClickListener(this)
        angle.setOnLongClickListener(this)
    }

    // button click handling
    override fun onClick(v: View) {
        when (v.id) {

            // change quote
            refresh.id -> {
                if (!loading) {
                    loading = true
                    getRandomQuote()
                } else
                    toast("please wait for loading to finish")
            }

            // quote gradient
            quoteGradient.id -> {
                if (!loading)
                    changeGradient()
                else
                    toast("please wait for loading to finish")
            }

            // author gradient
            authorGradient.id -> {
                if (!loading)
                    changeGradientAuthor()
                else
                    toast("please wait for loading to finish")
            }

            // change image
            changeImage.id -> {
                if (!loading)
                    changeImage()
                else
                    toast("please wait for loading to finish")
            }

            // align quote
            quoteAlign.id -> {
                if (!loading) {
                    changeAlignment((quoteObject.quoteAlign + 1) % 3)
                } else
                    toast("please wait for loading")
            }

            // align author
            authorAlign.id -> {
                if (!loading) {
                    changeAuthorAlignment((quoteObject.authorAlign + 1) % 3)
                } else
                    toast("please wait for loading")
            }

            // change gradient angle
            angle.id -> {
                if (!loading) {
                    gradientAngle((quoteObject.angle + 45) % 360)
                } else
                    toast("please wait for loading")
            }

            // edit button press
            edit.id -> {
                if (options.visibility == View.VISIBLE)
                    options.gone()
                else
                    options.show()
            }

            // download button press
            download.id -> {
                Permissions.askWriteExternalStoragePermission(this) { e, r ->
                    e?.let {

                    }
                    r?.let {
                        GlobalScope.launch {
                            val b = F.generateBitmap(this@MainActivity, quoteObject)
                            val file =
                                File(Environment.getExternalStorageDirectory().path, "abcdefg.jpg")
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

    // long click info
    override fun onLongClick(v: View): Boolean {
        when (v.id) {
            refresh.id -> toast("fetch a new quote")
            download.id -> toast("download the current quote on screen")
            edit.id -> toast("edit the quote layout")
            quoteGradient.id -> toast("change the background gradient")
            authorGradient.id -> toast("change the author gradient")
            changeImage.id -> toast("change background image")
            quoteAlign.id -> toast("change alignment of quote")
            authorAlign.id -> toast("change alignment of author")
            angle.id -> toast("change gradient angle")
        }
        return true
    }

    // get random quotes
    private fun getRandomQuote() {

        progress.show()
        card.gone()

        F.getQuote(this) { pojo ->
            if (pojo != null) {

                ImageHandler.getBitmap(bitmap, this) {
                    if (it != null) {

                        loading = false

                        // get random angles & gradient colors
                        val colors = F.randomGradient().toIntArray()
                        val colorsAuthor = F.randomGradient().toIntArray()
                        val angle = Angles.random().toFloat()

                        // create quote object
                        quoteObject = ObjectQuote(
                            pojo.quote,
                            colors,
                            angle,
                            pojo.author,
                            colorsAuthor,
                            it,
                            ALIGN_LEFT,
                            ALIGN_LEFT
                        )

                        // set details
                        bitmap = it
                        background.setImageBitmap(it)
                        setBackground()

                        gradient.setGradient(colors, 0, angle)
                        blurMask.setGradient(colors, 0, angle)
                        authorLayout.setGradient(colorsAuthor, 16)
                        angleIcon(angle)

                        quote.text = pojo.quote
                        author.text = pojo.author

                        // change alignment
                        changeAlignment((0..2).random())
                        changeAuthorAlignment((0..2).random())

                        card.show()
                    } else {
                        toast("error fetching quote image")
                        loading = false
                    }

                    progress.gone()
                }
            } else {
                toast("error fetching quote")
                progress.gone()
                loading = false
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

        // set params
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
            0 -> {
                quoteAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_left))
                Gravity.LEFT
            }
            1 -> {
                quoteAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_center))
                Gravity.CENTER
            }
            else -> {
                quoteAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_right))
                Gravity.RIGHT
            }
        }
        quoteObject.quoteAlign = align
    }

    // change author alignment
    private fun changeAuthorAlignment(align: Int) {
        val params = authorCard.layoutParams as RelativeLayout.LayoutParams

        params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        params.removeRule(RelativeLayout.CENTER_HORIZONTAL)
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        when (align) {
            0 -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                authorAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_left))
            }
            1 -> {
                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                authorAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_center))
            }
            2 -> {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                authorAlignI.setImageDrawable(useDrawable(R.drawable.vd_align_right))
            }
        }

        authorCard.layoutParams = params
        quoteObject.authorAlign = align
    }

    // change gradient angle
    private fun gradientAngle(angle: Float) {
        gradient.setGradient(quoteObject.gradient, 0, angle)
        blurMask.setGradient(quoteObject.gradient, 0, angle)
        quoteObject.angle = angle
        angleIcon(angle)
    }

    // gradient angle icon
    private fun angleIcon(angle: Float) {
        when (angle) {
            0f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_lr))
            45f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_bl_tr))
            90f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_bt))
            135f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_br_tl))
            180f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_rl))
            225f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_tr_bl))
            270f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_tb))
            315f -> angleI.setImageDrawable(useDrawable(R.drawable.vd_gradient_tl_br))
        }
    }

    // change gradient design
    private fun changeGradient() {
        val colors = F.randomGradient().toIntArray()
        val angle = quoteObject.angle
        gradient.setGradient(colors, 0, angle)
        blurMask.setGradient(colors, 0, angle)

        quoteObject.gradient = colors
    }

    // change gradient author
    private fun changeGradientAuthor() {
        val colors = F.randomGradient().toIntArray()
        authorLayout.setGradient(colors)
        quoteObject.authorGradient = colors
    }

    // change image
    private fun changeImage() {
        loading = true
        progressImage.show()
        ImageHandler.getBitmap(bitmap, this) {
            if (it != null) {
                loading = false
                bitmap = it
                quoteObject.image = it
                background.setImageBitmap(it)
                setBackground()
            } else {
                loading = false
                toast("error fetching quote image")
            }

            progressImage.gone()
        }
    }
}