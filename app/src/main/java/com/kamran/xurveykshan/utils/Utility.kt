package com.kamran.xurveykshan.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

fun openAppInfo(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}


fun View.showKeyboard(flag : Boolean) {
    val intFlag = if(flag) InputMethodManager.SHOW_IMPLICIT else 0
    val inputMethodManager =
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({ inputMethodManager.showSoftInput(this, intFlag) }, 300)

    Log.d("LOGTAG", "show Keyboard $flag")
}