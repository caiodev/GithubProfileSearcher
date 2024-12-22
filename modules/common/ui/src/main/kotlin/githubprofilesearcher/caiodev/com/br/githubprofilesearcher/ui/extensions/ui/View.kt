package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.extensions.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.cast.castTo
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils.types.number.defaultInteger
import kotlinx.coroutines.launch

fun EditText.hideKeyboard() {
    context
        .getSystemService(Context.INPUT_METHOD_SERVICE)
        .castTo<InputMethodManager>()
        ?.hideSoftInputFromWindow(applicationWindowToken, defaultInteger())
}

inline fun LifecycleOwner.runTaskOnBackground(crossinline task: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) { task() }
    }
}

fun View.applyViewVisibility(visibility: Int) {
    this.visibility = visibility
}

fun Snackbar.showMessage(
    @StringRes message: Int,
) {
    setText(message)
    show()
}