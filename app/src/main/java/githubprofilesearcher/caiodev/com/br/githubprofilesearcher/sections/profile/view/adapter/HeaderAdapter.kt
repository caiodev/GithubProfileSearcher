package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.EmptyViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.databinding.HeaderViewHolderLayoutBinding
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.view.viewHolder.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty

class HeaderAdapter(private val headerName: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var viewType = empty

    internal fun updateViewState(viewType: Int) {
        this.viewType = viewType
    }

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == empty) {
            EmptyViewHolder(
                EmptyViewHolderLayoutBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        } else {
            HeaderViewHolder(
                HeaderViewHolderLayoutBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is HeaderViewHolder) viewHolder.bind(headerName)
    }

    companion object {
        const val header = 5
    }
}
