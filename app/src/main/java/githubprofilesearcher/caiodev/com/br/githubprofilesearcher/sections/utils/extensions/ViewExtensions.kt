package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

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

fun Context.hideKeyboard(editText: EditText) {
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}

//It pops up a message received as parameter
fun Context.toastMaker(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}