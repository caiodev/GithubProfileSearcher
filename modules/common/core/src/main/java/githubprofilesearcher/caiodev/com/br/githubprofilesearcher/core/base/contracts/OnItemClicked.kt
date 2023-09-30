package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.contracts

interface OnItemClicked {
    fun onItemClick(
        adapterPosition: Int,
        id: Int,
    )
}
