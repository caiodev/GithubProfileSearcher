package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.obtainDefaultString

data class SuccessWithoutBody(val message: String = obtainDefaultString()) : State<Success>
