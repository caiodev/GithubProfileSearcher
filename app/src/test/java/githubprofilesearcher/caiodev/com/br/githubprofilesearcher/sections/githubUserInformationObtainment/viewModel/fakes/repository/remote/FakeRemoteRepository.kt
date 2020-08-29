package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult

class FakeRemoteRepository {

    fun provideFakeResponse() = APICallResult.Success(
        GithubProfilesList(

            listOf(

                GithubProfileInformation(
                    "torvalds",
                    "https://github.com/torvalds",
                    1024025,
                    "https://avatars0.githubusercontent.com/u/1024025?v=4"
                )
            )
        )
    )
}
