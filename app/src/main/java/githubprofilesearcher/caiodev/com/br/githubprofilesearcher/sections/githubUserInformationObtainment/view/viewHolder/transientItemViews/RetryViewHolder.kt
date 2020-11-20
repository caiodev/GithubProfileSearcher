package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder.transientItemViews

import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.RetryViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry

class RetryViewHolder(
    itemBinding: RetryViewHolderLayoutBinding,
    private val onItemClicked: OnItemClicked?
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    init {
        itemBinding.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(layoutPosition, retry)
        }
    }
}
