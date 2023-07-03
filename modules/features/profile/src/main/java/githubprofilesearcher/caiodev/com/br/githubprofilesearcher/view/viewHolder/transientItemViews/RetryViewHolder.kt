package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews

import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.RetryViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.contracts.OnItemClicked

class RetryViewHolder(
    itemBinding: RetryViewHolderLayoutBinding,
    private val onItemClicked: OnItemClicked?,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    init {
        itemBinding.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(layoutPosition, retry)
        }
    }

    companion object {
        const val retry = 3
    }
}
