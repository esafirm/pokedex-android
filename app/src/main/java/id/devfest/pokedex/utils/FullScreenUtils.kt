package id.devfest.pokedex.utils

import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

object FullScreenUtils {
    fun apply(window: Window?) {
        window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    fun applyToolbar(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            view.updatePadding(
                top = view.paddingTop + insets.systemWindowInsetTop
            )
            insets.consumeSystemWindowInsets()
        }
    }
}