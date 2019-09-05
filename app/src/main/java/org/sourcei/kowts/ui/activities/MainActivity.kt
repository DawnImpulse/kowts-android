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
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import co.revely.gradient.RevelyGradient
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sourcei.kowts.R
import org.sourcei.kowts.ui.Model
import org.sourcei.kowts.utils.functions.*
import org.sourcei.kowts.utils.handler.ImageHandler

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
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var bitmap: Bitmap
    val model by lazy { Model(this) }

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFab()
        getRandomQuote()

        // click listeners
        settings.setOnClickListener(this)
        refresh.setOnClickListener(this)
        changeGradient.setOnClickListener(this)
        changeAuthor.setOnClickListener(this)
        changeImage.setOnClickListener(this)
        share.setOnClickListener(this)
        download.setOnClickListener(this)
    }

    // button click handling
    override fun onClick(v: View) {
        when(v.id){

            settings.id -> { }

            refresh.id -> {
                progress.show()
                card.gone()
                GlobalScope.launch {
                    delay(1500)
                    runOnUiThread { getRandomQuote() }
                }
            }

            changeGradient.id -> {}

            changeAuthor.id -> {}

            changeImage.id -> {}

            share.id -> {}

            download.id -> {}

        }
    }

    // get random quotes
    private fun getRandomQuote() {

        model.getRandomQuote { e, r ->

            e?.let {
                loge(e)
                toast("error fetching quote")
                progress.gone()
            }
            r?.let {pojo ->
                setDimensions(F.displayDimensions(this))
                ImageHandler.getBitmap(this) {
                    if (it != null) {
                        bitmap = it
                        background.setImageBitmap(it)
                        setBackground()

                        val colors = F.randomGradient().toIntArray()
                        RevelyGradient.linear().colors(colors).onBackgroundOf(gradient)
                        RevelyGradient.linear().colors(colors).onBackgroundOf(blurMask)
                        RevelyGradient.linear().colors(F.randomGradient().toIntArray()).onBackgroundOf(authorLayout)
                        quote.text = pojo.quote
                        author.text = pojo.author

                        card.show()
                    } else
                        toast("error fetching quote image")

                    progress.gone()
                }
            }
        }
    }

    // set card & text dimensions
    private fun setDimensions(point: Point) {

        // set dimensions for card
        val x = point.x - F.dpToPx(32, this)
        val y = x + x / 4

        val params = RelativeLayout.LayoutParams(x, y)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        card.layoutParams = params


        // set dimensions for quote & author layout

        // original params
        val paramsT = quote.layoutParams // original params for quote
        val paramsA = authorCard.layoutParams // original params for author

        // new params
        val paramsNQ = RelativeLayout.LayoutParams(paramsT.width, y / 2)
        val paramsNA = RelativeLayout.LayoutParams(paramsA.width, paramsA.height)

        paramsNQ.setMargins(F.dpToPx(16, this))
        paramsNA.setMargins(F.dpToPx(16, this))
        paramsNA.addRule(RelativeLayout.BELOW, R.id.quote)


        // random alignment
        val random = (0..2).random()
        when (random) {
            // left
            0 -> {
                paramsNA.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

                // set alignment
                authorCard.layoutParams = paramsNA
                quote.layoutParams = paramsNQ
                quote.gravity = Gravity.LEFT
            }
            // center
            1 -> {
                paramsNA.addRule(RelativeLayout.CENTER_HORIZONTAL)

                // set alignment
                authorCard.layoutParams = paramsNA
                quote.layoutParams = paramsNQ
                quote.gravity = Gravity.CENTER
            }
            // right
            2 -> {
                paramsNA.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

                // set alignment
                authorCard.layoutParams = paramsNA
                quote.layoutParams = paramsNQ
                quote.gravity = Gravity.RIGHT
            }
        }
    }

    // set background
    private fun setBackground() {
        Blurry.with(this)
                .async()
                .sampling(2)
                .from(bitmap)
                .into(blurBg)
    }

    // set fab
    private fun setFab() {
        val colors = F.randomGradient().toIntArray()
        RevelyGradient.linear().colors(colors).onBackgroundOf(fab)
    }
}