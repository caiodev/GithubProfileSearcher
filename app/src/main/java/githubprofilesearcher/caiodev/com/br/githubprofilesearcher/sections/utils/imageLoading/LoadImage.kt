package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.imageLoading

import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

object LoadImage {

    fun loadImage(
        activity: FragmentActivity,
        imageUrl: String,
        errorImage: Int,
        cacheStrategy: DiskCacheStrategy,
        priority: Priority,
        transition: DrawableTransitionOptions,
        targetImageView: ImageView
    ) {

        Glide.with(activity)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .error(errorImage) // will be displayed if the image cannot be loaded
                    .diskCacheStrategy(cacheStrategy)
                    .priority(priority)
            )
            .transition(transition)
            .into(targetImageView)
    }
}