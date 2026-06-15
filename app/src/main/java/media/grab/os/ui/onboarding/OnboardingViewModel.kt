package media.grab.os.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import media.grab.os.data.prefs.OnboardingRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repo: OnboardingRepository) : ViewModel() {
    fun complete() { viewModelScope.launch { repo.completeOnboarding() } }
}