package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay.Delay.delayTask
import kotlinx.android.synthetic.main.custom_snackbar_layout.view.*

class CustomSnackBar(
    parent: ViewGroup,
    content: View,
    callback: com.google.android.material.snackbar.ContentViewCallback
) : BaseTransientBottomBar<CustomSnackBar>(parent, content, callback) {

    private val snackBarParentLayout =
        getView().findViewById(R.id.snackBarParentLinearLayout) as LinearLayout
    private val snackBarText = getView().findViewById(R.id.snackBarTextView) as TextView
    private var hasSnackBarBeenRequestedToDismiss = false

    fun setText(text: Int): CustomSnackBar {
        snackBarText.text = snackBarParentLayout.context.getString(text)
        return this
    }

    fun setBackgroundColor(backgroundColor: Int): CustomSnackBar {
        snackBarParentLayout.snackBarParentLinearLayout.setBackgroundColor(backgroundColor)
        return this
    }

    override fun dismiss() {
        if (!hasSnackBarBeenRequestedToDismiss) {
            hasSnackBarBeenRequestedToDismiss = true
            delayTask(3000) {
                super.dismiss()
                hasSnackBarBeenRequestedToDismiss = false
            }
        }
    }

    companion object {

        fun make(parent: ViewGroup): CustomSnackBar {
            val content =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.custom_snackbar_layout, parent, false)

            return CustomSnackBar(
                parent,
                content,
                CustomContentViewCallback()
            ).run {
                getView().setPadding(0, 0, 0, 0)
                this.duration = Snackbar.LENGTH_INDEFINITE
                this.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                this
            }
        }
    }
}
