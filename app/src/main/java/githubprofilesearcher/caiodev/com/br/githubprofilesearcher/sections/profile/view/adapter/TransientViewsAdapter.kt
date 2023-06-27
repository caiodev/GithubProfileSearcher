package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.EmptyViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.EndOfResultsViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.LoadingViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.RetryViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EmptyViewHolder.Companion.empty
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EndOfResultsViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EndOfResultsViewHolder.Companion.endOfResults
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.LoadingViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.LoadingViewHolder.Companion.loading
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.RetryViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.RetryViewHolder.Companion.retry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.OnItemClicked

class TransientViewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClicked: OnItemClicked
    private var viewType = empty

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            loading -> {
                LoadingViewHolder(
                    LoadingViewHolderLayoutBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                )
            }

            retry -> {
                RetryViewHolder(
                    RetryViewHolderLayoutBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                    itemClicked,
                )
            }

            endOfResults -> EndOfResultsViewHolder(
                EndOfResultsViewHolderLayoutBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )

            else ->
                EmptyViewHolder(
                    EmptyViewHolderLayoutBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                )
        }
    }

    override fun getItemCount() = 1

    internal fun updateViewState(newState: Int) {
        viewType = newState
    }

    internal fun setOnItemClicked(onItemClicked: OnItemClicked) {
        itemClicked = onItemClicked
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // detekt : Empty block
    }
}
