package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.HeaderViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolders.transientItemViews.EmptyViewHolder
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.empty

class HeaderAdapter(private val headerName: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var viewType = empty

    internal fun updateViewState(viewType: Int) {
        this.viewType = viewType
    }

    override fun getItemViewType(position: Int) = viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            empty -> EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.empty_view_holder_layout,
                    parent,
                    false
                )
            )

            else -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.header_view_holder_layout,
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
}