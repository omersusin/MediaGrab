package media.grab.os.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import media.grab.os.R
import media.grab.os.ds.components.MGFilledButton
import media.grab.os.ds.components.MGOutlinedButton
import media.grab.os.navigation.Destinations

@Composable
fun OnboardingScreen(navController: NavController, vm: OnboardingViewModel = hiltViewModel()) {
    val pages = listOf(
        OnboardingPage(Icons.Rounded.Download, stringResource(R.string.onboarding_title_1), stringResource(R.string.onboarding_desc_1)),
        OnboardingPage(Icons.Rounded.Lock, stringResource(R.string.onboarding_title_2), stringResource(R.string.onboarding_desc_2)),
        OnboardingPage(Icons.Rounded.RocketLaunch, stringResource(R.string.onboarding_title_3), stringResource(R.string.onboarding_desc_3))
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                OnboardingPageContent(pages[page])
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PageDots(current = pagerState.currentPage, total = pages.size)
            }
            val onLast = pagerState.currentPage == pages.size - 1
            if (onLast) {
                MGFilledButton(text = stringResource(R.string.onboarding_cta), onClick = { vm.complete(); navController.navigate(Destinations.Home.route) { popUpTo(Destinations.Onboarding.route) { inclusive = true } } }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MGFilledButton(text = stringResource(R.string.onboarding_cta), onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }, modifier = Modifier.fillMaxWidth())
                    MGOutlinedButton(text = stringResource(R.string.onboarding_skip), onClick = { vm.complete(); navController.navigate(Destinations.Home.route) { popUpTo(Destinations.Onboarding.route) { inclusive = true } } }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

private data class OnboardingPage(val icon: ImageVector, val title: String, val description: String)

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Icon(page.icon, contentDescription = null, modifier = Modifier.size(160.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Text(page.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 32.dp))
        Text(page.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PageDots(current: Int, total: Int) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            Box(modifier = Modifier.size(if (i == current) 10.dp else 8.dp).padding(2.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(if (i == current) 10.dp else 8.dp)) {
                    drawCircle(color = if (i == current) androidx.compose.ui.graphics.Color(0xFF4FD8EB) else androidx.compose.ui.graphics.Color(0xFF6B7280))
                }
            }
        }
    }
}