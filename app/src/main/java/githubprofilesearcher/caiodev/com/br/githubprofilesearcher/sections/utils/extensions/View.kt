package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.addRepeatingJob
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar.Companion.shouldSnackBarBeShownIfUserIsOnline

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
    crossinline onDismissed: (() -> Any) = {}
) {
    with(this) {
        setText(message)
        addCallback(
            object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    onDismissed()
                }
            }
        )
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
            if (shouldSnackBarBeShownIfUserIsOnline) {
                show()
            } else {
                shouldSnackBarBeShownIfUserIsOnline = true
            }
        }
    }
}

fun EditText.hideKeyboard() {
    with(this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(this@hideKeyboard.applicationWindowToken, 0)
    }
}

@Suppress("UNUSED")
fun LifecycleOwner.runTaskOnBackground(task: suspend () -> Unit) =
    this@runTaskOnBackground.addRepeatingJob(Lifecycle.State.STARTED) {
        task()
    }

@Suppress("UNUSED")
inline fun <reified T> AppCompatActivity.castValue(value: Any?) = value as T
