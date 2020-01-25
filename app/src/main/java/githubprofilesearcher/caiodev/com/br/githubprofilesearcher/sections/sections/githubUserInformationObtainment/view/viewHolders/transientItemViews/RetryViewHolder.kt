package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import kotlinx.android.synthetic.main.retry_view_holder_layout.view.*

class RetryViewHolder(itemView: View, private val onItemClicked: OnItemClicked?) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.retryTextViewLayoutParent.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, retry)
        }
    }
}