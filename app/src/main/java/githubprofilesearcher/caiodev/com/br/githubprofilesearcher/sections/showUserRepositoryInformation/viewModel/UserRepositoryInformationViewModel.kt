package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.showUserRepositoryInformation.viewModel

import android.content.pm.ResolveInfo
import androidx.lifecycle.ViewModel

class UserRepositoryInformationViewModel : ViewModel() {

    internal fun isChromeInstalled(resolveInfoList: List<ResolveInfo>): Boolean {

        var isChromeInstalled = false

        val packageCount = resolveInfoList.count {
            it.activityInfo.packageName.contains(stable) ||
                it.activityInfo.packageName.contains(beta) ||
                it.activityInfo.packageName.contains(dev) ||
                it.activityInfo.packageName.contains(local)
        }

        if (packageCount >= 1) {
            isChromeInstalled = true
        }

        return isChromeInstalled
    }

    companion object {
        private const val stable = "com.android.chrome"
        private const val beta = "com.chrome.beta"
        private const val dev = "com.chrome.dev"
        private const val local = "com.google.android.apps.chrome"
    }
}
