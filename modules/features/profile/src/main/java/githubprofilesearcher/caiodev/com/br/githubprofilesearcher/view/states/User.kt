package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.states

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.states.UIState

data class User(val content: List<UserProfile>) : UIState
