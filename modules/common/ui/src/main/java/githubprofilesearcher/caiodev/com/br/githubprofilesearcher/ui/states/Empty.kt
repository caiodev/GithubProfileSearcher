package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states

import androidx.annotation.StringRes

data class Empty(
    @StringRes val message: Int,
) : UIState
