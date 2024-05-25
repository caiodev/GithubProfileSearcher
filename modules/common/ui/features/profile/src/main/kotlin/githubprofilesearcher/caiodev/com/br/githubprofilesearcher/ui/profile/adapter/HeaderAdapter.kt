package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.databinding.EmptyViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.databinding.HeaderViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.profile.viewHolder.transientItemViews.EmptyViewHolder.Companion.EMPTY

class HeaderAdapter(private val headerName: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var viewType = EMPTY

    internal fun updateViewState(viewType: Int) {
        this.viewType = viewType
    }

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == EMPTY) {
            EmptyViewHolder(
                EmptyViewHolderLayoutBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )
        } else {
            HeaderViewHolder(
                HeaderViewHolderLayoutBinding.inflate(
                    inflater,
                    parent,
                    false,
                ),
            )
        }
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (viewHolder is HeaderViewHolder) viewHolder.bind(headerName)
    }

    companion object {
        const val HEADER = 5
    }
}
