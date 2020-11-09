package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.view.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.header_view_holder_layout.view.*

class HeaderViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    internal fun bind(model: Int) {
        itemView.githubUsersListHeader.text =
            itemView.context.getString(model)
    }
}
