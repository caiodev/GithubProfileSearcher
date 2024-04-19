package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews

import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.contracts.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.RetryViewHolderLayoutBinding

class RetryViewHolder(
    itemBinding: RetryViewHolderLayoutBinding,
    private val onItemClicked: OnItemClicked?,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    init {
        itemBinding.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(layoutPosition, RETRY)
        }
    }

    companion object {
        const val RETRY = 3
    }
}
