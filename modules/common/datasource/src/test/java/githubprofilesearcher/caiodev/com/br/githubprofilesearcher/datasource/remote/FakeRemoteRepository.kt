package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.models.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.models.profile.UserProfile

class FakeRemoteRepository {

    fun provideFakeResponse() = States.Success(
        Profile(

            listOf(

                UserProfile(
                    "torvalds",
                    "https://github.com/torvalds",
                    1024025,
                    "https://avatars0.githubusercontent.com/u/1024025?v=4",
                ),
            ),
        ),
    )
}
