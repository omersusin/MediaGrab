package media.grab.os.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import media.grab.os.data.model.Download
import media.grab.os.data.repository.DownloadRepository
import javax.inject.Inject

data class HomeUiState(val active: List<Download> = emptyList(), val recent: List<Download> = emptyList())

@HiltViewModel
class HomeViewModel @Inject constructor(repo: DownloadRepository) : ViewModel() {
    val state: StateFlow<HomeUiState> = kotlinx.coroutines.flow.combine(repo.observeActive(), repo.observeRecent(5)) { active, recent -> HomeUiState(active, recent) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState())
}