package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import kotlinx.coroutines.launch

@Suppress("UNUSED")
fun View.applyViewVisibility(visibility: Int) {
    this.visibility = visibility
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

fun ImageView.changeDrawable(newDrawable: Int) {
    this.setImageDrawable(
        ContextCompat.getDrawable(
            this.context,
            newDrawable
        )
    )
}

fun View.applyBackgroundColor(color: Int) {
    this.setBackgroundColor(ContextCompat.getColor(this.context, color))
}

@Suppress("UNUSED")
inline fun Snackbar.showErrorSnackBar(
    message: Int,
    crossinline onDismissed: (() -> Any) = { emptyString }
) {
    with(this) {
        setText(message)
        if (onDismissed() is Unit) {
            addCallback(
                object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        onDismissed.invoke()
                    }
                }
            )
        }
        show()
    }
}

fun CustomSnackBar.showInternetConnectionStatusSnackBar(
    isInternetConnectionAvailable: Boolean
) {
    with(this) {
        if (isInternetConnectionAvailable) {
            setText(R.string.back_online_success_message).setBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.green_700
                )
            )
            if (isShown) dismiss()
        } else {
            setText(R.string.no_connection_error).setBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.red_700
                )
            )
            show()
        }
    }
}

fun EditText.hideKeyboard() {
    with(this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(this@hideKeyboard.applicationWindowToken, 0)
    }
}

fun LifecycleOwner.runTaskOnBackground(task: suspend () -> Unit) {
    lifecycleScope.launch {
        task.invoke()
    }
}

inline fun <reified T> AppCompatActivity.castValue(value: Any?) = value as T
