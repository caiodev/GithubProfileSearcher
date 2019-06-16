package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

object LoadImage {

    fun loadImage(
        context: Context,
        imageUrl: String,
        errorImage: Int,
        targetImageView: ImageView
    ) {
        Glide.with(context)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .error(errorImage) // will be displayed if the image cannot be loaded
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.IMMEDIATE)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(targetImageView)
    }
}