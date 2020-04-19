package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar

@Suppress("unused")
fun Context.applyViewVisibility(view: View, visibility: Int? = null) {

    when (view) {

        is SwipeRefreshLayout -> {
            if (view.isRefreshing) view.isRefreshing = false
            if (!view.isEnabled) view.isEnabled = true
        }

        else -> visibility?.let {
            view.visibility = it
        }
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

@Suppress("unused")
inline fun Context.showSnackBar(
    fragmentActivity: FragmentActivity, message: String, crossinline onDismissed: () -> Unit
) {
    Snackbar.make(
        fragmentActivity.findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_SHORT
    ).addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            onDismissed.invoke()
        }
    }).show()
}

fun Context.hideKeyboard(editText: EditText) {
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}

@Suppress("unused")
fun Context.setViewXYScales(view: View, xAxis: Float, yAxis: Float) {
    view.scaleX = xAxis
    view.scaleY = yAxis
}