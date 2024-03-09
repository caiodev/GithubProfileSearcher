package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.contracts.OnItemClicked
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.EmptyViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.EndOfResultsViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.LoadingViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.databinding.RetryViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.EmptyViewHolder.Companion.EMPTY
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.EndOfResultsViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.EndOfResultsViewHolder.Companion.END_OF_RESULTS
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.LoadingViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.LoadingViewHolder.Companion.LOADING
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.RetryViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.view.viewHolder.transientItemViews.RetryViewHolder.Companion.RETRY

class TransientViewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var itemClicked: OnItemClicked
    private var viewType = EMPTY

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADING ->
                LoadingViewHolder(
                    LoadingViewHolderLayoutBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                )

            RETRY ->
                RetryViewHolder(
                    RetryViewHolderLayoutBinding.inflate(
                        inflater,
                        parent,
                        false,
                    ),
                    itemClicked,
                )

            END_OF_RESULTS ->
                EndOfResultsViewHolder(
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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) = Unit
}
