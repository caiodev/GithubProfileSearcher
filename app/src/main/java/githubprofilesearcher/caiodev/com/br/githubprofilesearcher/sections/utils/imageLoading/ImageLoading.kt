package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy

object ImageLoading {

    private lateinit var imageLoader: ImageLoader

    fun loadImage(
        context: Context,
        imageUrl: String,
        @DrawableRes placeholder: Int,
        targetImageView: ImageView
    ) {
        if (!this::imageLoader.isInitialized) {
            imageLoader = ImageLoader.Builder(context)
                .crossfade(true)
                .placeholder(placeholder)
                .diskCachePolicy(CachePolicy.ENABLED)
                .error(placeholder)
                .build()
        }

        targetImageView.load(imageUrl, imageLoader)
    }
}
