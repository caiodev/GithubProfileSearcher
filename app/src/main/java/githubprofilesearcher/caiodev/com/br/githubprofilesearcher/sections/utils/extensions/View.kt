package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar

@Suppress("UNUSED")
fun Context.applyViewVisibility(view: View, visibility: Int) {
    view.visibility = visibility
}

@Suppress("UNUSED")
fun SwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(
    isSwipeRefreshing: Boolean = false,
    isSwipeEnabled: Boolean = true
) {
    this.apply {
        isRefreshing = isSwipeRefreshing
        isEnabled = isSwipeEnabled
    }
}

fun Context.changeDrawable(target: ImageView, newDrawable: Int) {
    target.setImageDrawable(
        ContextCompat.getDrawable(
            applicationContext,
            newDrawable
        )
    )
}

fun Context.applyBackgroundColor(view: View, color: Int) {
    view.setBackgroundColor(ContextCompat.getColor(applicationContext, color))
}

@Suppress("UNUSED")
inline fun Context.showErrorSnackBar(
    snackBar: Snackbar, message: Int, crossinline onDismissed: (() -> Any) = { emptyString }
) {
    with(snackBar) {
        setText(message)
        if (onDismissed() is Unit) {
            addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    onDismissed.invoke()
                }
            })
        }
        show()
    }
}

fun Context.showInternetConnectionStatusSnackBar(
    customSnackBar: CustomSnackBar,
    isInternetConnectionAvailable: Boolean
) {
    with(customSnackBar) {
        if (isInternetConnectionAvailable) {
            setText(R.string.back_online_success_message).setBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.green_700
                )
            )
            if (isShown) dismiss()
        } else {
            setText(R.string.no_connection_error).setBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.red_700
                )
            )
            show()
        }
    }
}

fun Context.hideKeyboard(editText: EditText) {
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}

@Suppress("UNUSED")
fun Context.setViewXYScales(view: View, xAxis: Float, yAxis: Float) {
    view.scaleX = xAxis
    view.scaleY = yAxis
}