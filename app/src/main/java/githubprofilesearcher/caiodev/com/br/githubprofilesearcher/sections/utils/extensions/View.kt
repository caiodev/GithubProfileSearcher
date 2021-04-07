package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.addRepeatingJob
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar.CustomSnackBar

fun CustomSnackBar.showInternetConnectionStatusSnackBar(
    isInternetConnectionAvailable: Boolean
) {
    if (isInternetConnectionAvailable) {
        setText(R.string.back_online_success_message).setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.green_700
            )
        )
        if (isShown) {
            dismiss()
        }
    } else {
        setText(R.string.no_connection_error).setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.red_700
            )
        )
        if (CustomSnackBar.shouldSnackBarBeShownIfUserIsOnline) {
            show()
        } else {
            CustomSnackBar.shouldSnackBarBeShownIfUserIsOnline = true
        }
    }
}

fun EditText.hideKeyboard() {
    castValue<InputMethodManager>(context.getSystemService(Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(applicationWindowToken, 0)
}

fun ImageView.changeDrawable(@DrawableRes newDrawable: Int) {
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            newDrawable
        )
    )
}

@Suppress("UNUSED")
fun LifecycleOwner.runTaskOnBackground(task: suspend () -> Unit) =
    addRepeatingJob(Lifecycle.State.STARTED) {
        task()
    }

fun View.applyBackgroundColor(@ColorRes color: Int) {
    setBackgroundColor(ContextCompat.getColor(context, color))
}

@Suppress("UNUSED")
fun View.applyViewVisibility(visibility: Int) {
    View.VISIBLE
    this.visibility = visibility
}

@Suppress("UNUSED")
inline fun Snackbar.showErrorSnackBar(
    @StringRes message: Int,
    crossinline onDismissed: (() -> Any) = {}
) {
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

@Suppress("UNUSED")
fun SwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(
    isSwipeRefreshing: Boolean = false,
    isSwipeEnabled: Boolean = true
) {
    isRefreshing = isSwipeRefreshing
    isEnabled = isSwipeEnabled
}
