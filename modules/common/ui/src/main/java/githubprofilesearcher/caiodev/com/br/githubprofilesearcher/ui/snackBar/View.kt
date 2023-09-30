package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.snackBar

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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting.castTo
import kotlinx.coroutines.launch

fun EditText.hideKeyboard() {
    castTo<InputMethodManager>(context.getSystemService(Context.INPUT_METHOD_SERVICE))
        ?.hideSoftInputFromWindow(applicationWindowToken, 0)
}

fun ImageView.changeDrawable(
    @DrawableRes newDrawable: Int,
) {
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            newDrawable,
        ),
    )
}

fun LifecycleOwner.runTaskOnBackground(task: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) { task() }
    }
}

fun View.applyBackgroundColor(
    @ColorRes color: Int,
) {
    setBackgroundColor(ContextCompat.getColor(context, color))
}

fun View.applyViewVisibility(visibility: Int) {
    this.visibility = visibility
}

fun Snackbar.showErrorSnackBar(
    @StringRes message: Int,
) {
    setText(message)
    show()
}

fun SwipeRefreshLayout.applySwipeRefreshVisibilityAttributes(isSwipeEnabled: Boolean = true) {
    isEnabled = isSwipeEnabled
}
