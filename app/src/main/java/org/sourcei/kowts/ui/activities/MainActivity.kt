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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.revely.gradient.RevelyGradient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.inflator_quote.*
import org.sourcei.kowts.R
import org.sourcei.kowts.ui.Model
import org.sourcei.kowts.utils.functions.F
import org.sourcei.kowts.utils.functions.loge
import org.sourcei.kowts.utils.functions.toast

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-08-20 by Saksham
 * @note Updates :
 */
class MainActivity : AppCompatActivity() {

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inflator_quote)

        Model(this).getRandomQuote { e, r ->
            e?.let {
                loge(e)
                toast("error")
            }
            r?.let {
                Glide.with(this).asBitmap().load("https://source.unsplash.com/random").diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background)
                RevelyGradient.linear().colors(F.randomGradient().toIntArray()).onBackgroundOf(gradient)
                RevelyGradient.linear().colors(F.randomGradient().toIntArray()).onBackgroundOf(authorLayout)
                quote.text = it.quote
                author.text = it.author
            }
        }
    }
}