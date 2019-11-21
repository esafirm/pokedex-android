package id.devfest.pokedex.utils

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

enum class InsetType {
    WINDOWS,
    ALL_GESTURE,
    GESTURE,
    MANDATORY_GESTURE
}

object FullScreenUtils {
    fun applyInsetMarks(rootView: ViewGroup, type: InsetType = InsetType.WINDOWS) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->

            val color = Color.parseColor("#1100FF00")
            val leftMark = View(rootView.context).apply {
                setBackgroundColor(color)
                layoutParams = ViewGroup.LayoutParams(
                    insets.systemGestureInsets.left,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val rightMark = View(rootView.context).apply {
                setBackgroundColor(color)

                if (rootView is RelativeLayout) {
                    layoutParams = RelativeLayout.LayoutParams(
                        insets.systemGestureInsets.left,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    }
                } else {
                    layoutParams = CoordinatorLayout.LayoutParams(
                        insets.systemGestureInsets.right,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        gravity = GravityCompat.END
                    }
                }
            }

            val bottomMark = View(rootView.context).apply {
                setBackgroundColor(Color.parseColor("#11CC0000"))

                if (rootView is CoordinatorLayout) {
                    layoutParams = CoordinatorLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        insets.mandatorySystemGestureInsets.bottom
                    ).apply {
                        gravity = Gravity.BOTTOM
                    }
                }
            }

            when (type) {
                InsetType.ALL_GESTURE -> {
                    rootView.addView(leftMark)
                    rootView.addView(rightMark)
                    rootView.addView(bottomMark)
                }
                InsetType.GESTURE -> {
                    rootView.addView(leftMark)
                    rootView.addView(rightMark)
                }
                InsetType.MANDATORY_GESTURE -> {
                    rootView.addView(bottomMark)
                }
                InsetType.WINDOWS -> setupWindowInsets(rootView, insets)
            }

            insets
        }
    }

    private fun setupWindowInsets(rootView: ViewGroup, insets: WindowInsetsCompat) {
        addInsetView(rootView, width = insets.systemWindowInsetLeft)
        addInsetView(rootView, width = insets.systemWindowInsetRight, gravity = GravityCompat.END)
        addInsetView(rootView, height = insets.systemWindowInsetBottom, gravity = Gravity.BOTTOM)
        addInsetView(rootView, height = insets.systemWindowInsetTop)
    }

    private fun addInsetView(
        rootView: ViewGroup,
        width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
        height: Int = ViewGroup.LayoutParams.MATCH_PARENT,
        gravity: Int = GravityCompat.START,
        color: Int = Color.YELLOW
    ) {
        val markView = View(rootView.context).apply {
            setBackgroundColor(color)
            if (rootView is CoordinatorLayout) {
                layoutParams = CoordinatorLayout.LayoutParams(width, height).apply {
                    this.gravity = gravity
                }
            }
        }
        rootView.elevation = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10F, rootView.context.resources.displayMetrics
        )
        rootView.addView(markView)
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