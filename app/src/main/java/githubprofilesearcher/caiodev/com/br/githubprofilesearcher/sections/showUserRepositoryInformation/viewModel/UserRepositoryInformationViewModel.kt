package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.viewModel

import android.content.pm.ResolveInfo
import androidx.lifecycle.ViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.beta
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.dev
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.local
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants.stable

class UserRepositoryInformationViewModel : ViewModel() {

    internal fun isChromeInstalled(resolveInfoList: List<ResolveInfo>): Boolean {

        var isChromeInstalled = false

        resolveInfoList.forEach {
            if (it.activityInfo.packageName.contains(stable) ||
                it.activityInfo.packageName.contains(beta) ||
                it.activityInfo.packageName.contains(dev) ||
                it.activityInfo.packageName.contains(local)
            ) {
                isChromeInstalled = true
            }
        }

        return isChromeInstalled
    }
}
