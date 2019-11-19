package id.devfest.pokedex.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

object FullScreenUtils {
    fun applyInsetMarks(rootView: ViewGroup) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, inset ->

            val color = Color.parseColor("#1100FF00")
            val leftMark = View(rootView.context).apply {
                setBackgroundColor(color)
                layoutParams = ViewGroup.LayoutParams(
                    inset.systemGestureInsets.left,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val rightMark = View(rootView.context).apply {
                setBackgroundColor(color)

                if (rootView is RelativeLayout) {
                    layoutParams = RelativeLayout.LayoutParams(
                        inset.systemGestureInsets.left,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    }
                } else {
                    layoutParams = CoordinatorLayout.LayoutParams(
                        inset.systemGestureInsets.right,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        gravity = GravityCompat.END
                    }
                }
            }

            rootView.addView(leftMark)
            rootView.addView(rightMark)

            inset
        }
    }

    fun apply(view: View?) {
        if (view != null) {
            view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
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