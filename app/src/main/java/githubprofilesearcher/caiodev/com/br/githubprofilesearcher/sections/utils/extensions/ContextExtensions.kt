package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar

@Suppress("UNUSED")
fun Context.setViewVisibility(view: View, visibility: Int? = null) {

    when (view) {

        is SwipeRefreshLayout -> {
            if (view.isRefreshing) view.isRefreshing = false
        }

        else -> {
            visibility?.let {
                view.visibility = it
            }
        }
    }
}

@Suppress("UNUSED")
fun Context.showSnackBar(
    fragmentActivity: FragmentActivity, message: String
) {
    Snackbar.make(
        fragmentActivity.findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_LONG
    ).show()
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