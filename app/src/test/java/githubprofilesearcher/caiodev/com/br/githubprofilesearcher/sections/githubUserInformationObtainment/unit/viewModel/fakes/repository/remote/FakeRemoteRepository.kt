package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.UserProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.Profile

class FakeRemoteRepository {

    fun provideFakeResponse() = States.Success(
        Profile(

            listOf(

                UserProfileInformation(
                    "torvalds",
                    "https://github.com/torvalds",
                    1024025,
                    "https://avatars0.githubusercontent.com/u/1024025?v=4"
                )
            )
        )
    )
}
