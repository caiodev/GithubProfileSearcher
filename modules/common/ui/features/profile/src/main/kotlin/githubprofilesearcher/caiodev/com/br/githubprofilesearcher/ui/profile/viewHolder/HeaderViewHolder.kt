package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.databinding.HeaderViewHolderLayoutBinding

class HeaderViewHolder(private val itemBinding: HeaderViewHolderLayoutBinding) :
    ViewHolder(itemBinding.root) {
    internal fun bind(
        @StringRes model: Int,
    ) {
        itemBinding.userListHeader.text =
            itemView.context.getString(model)
    }
}
