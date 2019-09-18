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
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import org.json.JSONObject
import org.sourcei.kowts.BuildConfig
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-08-20 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 12 - master - set gradient on view
 *  Saksham - 2019 09 14 - master - use drawable
 */

// int color to hexa string
fun Int.toHexa(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}




// gone view
fun View.gone() {
    visibility = View.GONE
}

// hide view
fun View.hide() {
    visibility = View.INVISIBLE
}

// gone view
fun View.show() {
    visibility = View.VISIBLE
}

// change layout parameters
fun View.setParams(width: Int, height: Int) {
    val parent: ViewParent = parent
    if (parent is FrameLayout)
        layoutParams = FrameLayout.LayoutParams(width, height)
    if (parent is RelativeLayout)
        layoutParams = RelativeLayout.LayoutParams(width, height)
    if (parent is LinearLayout)
        layoutParams = LinearLayout.LayoutParams(width, height)
}

// set gradient on view
fun View.setGradient(colors: IntArray, radius: Int = 0, angle: Float = 0F) {

    val bbg = if (background != null)
        background.current as GradientDrawable
    else
        GradientDrawable()

    bbg.cornerRadius = F.dpToPx(radius, context).toFloat()
    bbg.colors = colors

    bbg.orientation = when (angle) {
        45f -> GradientDrawable.Orientation.BL_TR
        90f -> GradientDrawable.Orientation.BOTTOM_TOP
        135f -> GradientDrawable.Orientation.BR_TL
        180f -> GradientDrawable.Orientation.RIGHT_LEFT
        225f -> GradientDrawable.Orientation.TR_BL
        270f -> GradientDrawable.Orientation.TOP_BOTTOM
        315f -> GradientDrawable.Orientation.TL_BR
        // for 0f
        else -> GradientDrawable.Orientation.LEFT_RIGHT
    }
    background = bbg
}





// open activity
fun <T> Context.openActivity(it: Class<T>) {
    startActivity(Intent(this, it))
}

// open activity
fun <T> Context.openActivity(it: Class<T>, bundle: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
}

// hide keyboard
fun Context.hideSoftKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// get drawable
fun Context.useDrawable(id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

//get display ratio a/b
fun Context.displayRatio(): Pair<Int, Int> {

    fun calculateHcf(width1: Int, height1: Int): Int {
        var width = width1
        var height = height1
        while (height != 0) {
            val t = height
            height = width % height
            width = t
        }
        return width
    }

    val point = F.displayDimensions(this)
    val hcf = calculateHcf(point.x, point.y)

    return Pair(point.y / hcf, point.x / hcf)
}

// toast
fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

// debug toast
fun Context.toastd(message: String, length: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG)
        Toast.makeText(this, message, length).show()
}

// start web
fun Context.startWeb(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}






// file path string to uri
fun String.toFileUri(): Uri {
    return Uri.fromFile(File(this))
}

// file path string to content uri
@Throws(Exception::class)
fun String.toContentUri(context: Context): Uri {
    try {
        return FileProvider.getUriForFile(context, "com.dawnimpulse.wallup", toFile())
    } catch (e: Exception) {
        throw e
    }
    //return this.toFileUri().toContentUri(context)
}

// tree uri path to file uri path
fun String.toFileString(): String {
    return if (this.contains(":")) {
        val substring = split(":")
        val tree = substring[0]

        if (tree.contains("primary"))
            Environment.getExternalStorageDirectory().path + "/${substring[1]}"
        else
            "/storage/${tree.replace("/tree/", "")}/${substring[1]}"
    } else
        this
}

// for md5
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

//covert to file type
fun String.toFile(): File {
    return File(this)
}






//convert to content uri
fun Uri.toContentUri(context: Context): Uri {
    val cr = context.contentResolver
    val file = File(this.path)
    val imagePath = file.absolutePath
    val imageName: String? = null
    val imageDescription: String? = null
    val uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription)
    return Uri.parse(uriString)
}

// get mime type
fun Uri.getMime(context: Context): String? {
    val cr = context.contentResolver
    return cr.getType(this)
}







// put directly with shared preference object
fun SharedPreferences.putAny(name: String, any: Any) {
    when (any) {
        is String -> edit().putString(name, any).apply()
        is Int -> edit().putInt(name, any).apply()
        is Boolean -> edit().putBoolean(name, any).apply()
    }
}

fun SharedPreferences.remove(name: String) {
    edit().remove(name).apply()
}





// json put params
fun jsonOf(vararg pairs: Pair<String, Any>) = JSONObject().apply {
    pairs.forEach {
        put(it.first, it.second)
    }
}

// log messages
fun logd(message: Any) {
    if (BuildConfig.DEBUG)
        Log.d(
            "wallup",
            "${Exception().stackTrace[1].className.replace(
                "${BuildConfig.APPLICATION_ID}.",
                ""
            )} :: $message"
        )
}

fun loge(message: Any) {
    if (BuildConfig.DEBUG)
        Log.e(
            "wallup",
            "${Exception().stackTrace[1].className.replace(
                "${BuildConfig.APPLICATION_ID}.",
                ""
            )} :: $message"
        )
}