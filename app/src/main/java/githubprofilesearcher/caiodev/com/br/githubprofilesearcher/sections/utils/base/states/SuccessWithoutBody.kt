package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.string.emptyString

data class SuccessWithoutBody(val message: String = emptyString()) : State<Success>
