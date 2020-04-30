package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import kotlinx.android.synthetic.main.retry_view_holder_layout.view.*

class RetryViewHolder(itemView: View, private val onItemClicked: OnItemClicked?) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.retryTextViewParentLayout.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(layoutPosition, retry)
        }
    }
}