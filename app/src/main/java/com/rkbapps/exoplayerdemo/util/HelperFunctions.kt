package com.rkbapps.exoplayerdemo.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.rkbapps.exoplayerdemo.R

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun hideSystemBars(window: Window) {
    // Configure the behavior of the hidden system bars
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    // Hide both the status bar and the navigation bar
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
}

fun showSystemBars(window: Window) {
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
}


fun getFileIcon(format: String): Int {
    if (format.contains("mp4", ignoreCase = true)) return R.drawable.mp4
    if (format.contains("avi", ignoreCase = true)) return R.drawable.avi
    if (format.contains("mkv", ignoreCase = true)) return R.drawable.mkv
    if (format.contains("mov", ignoreCase = true)) return R.drawable.mov
    if (format.contains("f4v", ignoreCase = true)) return R.drawable.f4v
    if (format.contains("flv", ignoreCase = true)) return R.drawable.flv
    if (format.contains("wmv", ignoreCase = true)) return R.drawable.wmv
    if (format.contains("swf", ignoreCase = true)) return R.drawable.swf
    if (format.contains("3gp", ignoreCase = true)) return R.drawable.three_gp
    return R.drawable.video
}


