package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.view

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.viewTypes.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.paginationLoading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.interfaces.OnItemClicked
import kotlinx.android.synthetic.main.generic_view_holder_layout.view.*

class GenericViewHolder(itemView: View, private val onItemClicked: OnItemClicked?) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.genericLayoutParent.retryTextView.setOnClickListener {
            onItemClicked?.onItemClick(adapterPosition, retry)
        }
    }

    internal fun bind(model: Generic) {

        when (model.state) {

            paginationLoading -> {
                if (itemView.paginationProgressBar.visibility != VISIBLE &&
                    itemView.retryTextView.visibility != VISIBLE
                ) {
                    itemView.retryTextView.visibility = GONE
                    itemView.endOfResultsTextView.visibility = GONE
                    itemView.paginationProgressBar.visibility = VISIBLE
                }
            }

            retry -> if (itemView.retryTextView.visibility != VISIBLE) {
                itemView.endOfResultsTextView.visibility = GONE
                itemView.paginationProgressBar.visibility = GONE
                itemView.retryTextView.apply {
                    visibility = VISIBLE
                    text = itemView.context.getString(R.string.retry_button_message)
                }
            }

            else -> with(itemView.endOfResultsTextView) {
                if (visibility != VISIBLE
                ) {
                    itemView.paginationProgressBar.visibility = GONE
                    itemView.retryTextView.visibility = GONE
                    itemView.endOfResultsTextView.visibility = VISIBLE
                    text = itemView.context.getString(R.string.end_of_results)
                }
            }
        }
    }

    internal fun changeState(state: Int) {

        when (state) {

            paginationLoading -> {
                itemView.retryTextView.visibility = GONE
                itemView.endOfResultsTextView.visibility = GONE
                itemView.paginationProgressBar.visibility = VISIBLE
            }

            retry -> {
                itemView.paginationProgressBar.visibility = GONE
                itemView.retryTextView.apply {
                    visibility = VISIBLE
                    text = itemView.context.getString(R.string.retry_button_message)
                }
            }
        }
    }
}