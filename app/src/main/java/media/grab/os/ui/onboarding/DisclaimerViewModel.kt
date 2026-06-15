package media.grab.os.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import media.grab.os.data.prefs.OnboardingRepository
import javax.inject.Inject

@HiltViewModel
class DisclaimerViewModel @Inject constructor(private val repo: OnboardingRepository) : ViewModel() {
    val accepted: StateFlow<Boolean> = repo.disclaimerAccepted.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    fun accept() { viewModelScope.launch { repo.acceptDisclaimer() } }
}