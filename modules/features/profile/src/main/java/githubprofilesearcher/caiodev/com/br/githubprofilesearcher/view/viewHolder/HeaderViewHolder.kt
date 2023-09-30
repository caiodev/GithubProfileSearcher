package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.HeaderViewHolderLayoutBinding

class HeaderViewHolder(private val itemBinding: HeaderViewHolderLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    internal fun bind(
        @StringRes model: Int,
    ) {
        itemBinding.userListHeader.text =
            itemView.context.getString(model)
    }
}
