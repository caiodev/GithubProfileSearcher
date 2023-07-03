package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.string.emptyString

data class SuccessWithoutBody(val message: String = emptyString()) : State<Success>
