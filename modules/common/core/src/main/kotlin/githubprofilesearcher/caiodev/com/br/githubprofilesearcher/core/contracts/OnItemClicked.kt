package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.contracts

fun interface OnItemClicked {
    fun onItemClick(
        adapterPosition: Int,
        id: Int,
    )
}
