@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.grodnoroads.extensions

import android.content.Context
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.toast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

inline fun Context.drawableCompat(id: Int) = AppCompatResources.getDrawable(this, id)