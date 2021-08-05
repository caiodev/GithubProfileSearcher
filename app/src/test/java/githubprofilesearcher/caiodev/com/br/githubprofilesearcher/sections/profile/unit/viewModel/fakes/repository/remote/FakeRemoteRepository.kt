package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.Profile

class FakeRemoteRepository {

    fun provideFakeResponse() = States.Success(
        Profile(

            listOf(

                UserProfile(
                    "torvalds",
                    "https://github.com/torvalds",
                    1024025,
                    "https://avatars0.githubusercontent.com/u/1024025?v=4"
                )
            )
        )
    )
}
