package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.HeaderViewHolderLayoutBinding

class HeaderViewHolder(private val itemBinding: HeaderViewHolderLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    internal fun bind(model: Int) {
        itemBinding.githubUsersListHeader.text =
            itemView.context.getString(model)
    }
}
