package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.customViews.snackBar

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.CustomSnackbarLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.delay.Delay.delayTaskBy

class CustomSnackBar(
    content: ViewGroup,
    private val viewBinding: CustomSnackbarLayoutBinding,
    callback: com.google.android.material.snackbar.ContentViewCallback
) : BaseTransientBottomBar<CustomSnackBar>(content, viewBinding.root, callback) {
    private var hasSnackBarBeenRequestedToDismiss = false

    fun setText(text: Int): CustomSnackBar {
        viewBinding.snackBarTextView.text = viewBinding.root.context.getString(text)
        return this
    }

    fun setBackgroundColor(backgroundColor: Int): CustomSnackBar {
        viewBinding.snackBarParentLinearLayout.setBackgroundColor(backgroundColor)
        return this
    }

    override fun dismiss() {
        if (!hasSnackBarBeenRequestedToDismiss) {
            hasSnackBarBeenRequestedToDismiss = true
            delayTaskBy(taskDelay) {
                super.dismiss()
                hasSnackBarBeenRequestedToDismiss = false
            }
        }
    }

    companion object {
        private const val taskDelay = 3000L

        fun make(parent: ViewGroup): CustomSnackBar {
            val content = CustomSnackbarLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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
