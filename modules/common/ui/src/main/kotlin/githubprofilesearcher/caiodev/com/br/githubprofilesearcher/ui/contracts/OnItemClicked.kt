package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.contracts

fun interface OnItemClicked {
    fun onItemClick(
        adapterPosition: Int,
        id: Int,
    )
}
