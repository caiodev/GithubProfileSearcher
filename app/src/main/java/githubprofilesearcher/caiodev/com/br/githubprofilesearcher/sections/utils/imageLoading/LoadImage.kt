package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading

import android.widget.ImageView
import coil.api.load
import coil.request.CachePolicy

object LoadImage {

    fun loadImage(
        imageUrl: String,
        placeholder: Int,
        targetImageView: ImageView
    ) {
        targetImageView.load(imageUrl) {
            crossfade(true)
            placeholder(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .error(placeholder)
        }
    }
}