package media.grab.os.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import media.grab.os.R
import media.grab.os.ds.components.MGFilledButton
import media.grab.os.ds.components.MGTextButton
import media.grab.os.navigation.Destinations

@Composable
fun DisclaimerScreen(navController: NavController, vm: DisclaimerViewModel = hiltViewModel()) {
    val accepted by vm.accepted.collectAsStateWithLifecycle()
    LaunchedEffect(accepted) { if (accepted) navController.navigate(Destinations.Onboarding.route) { popUpTo(Destinations.Disclaimer.route) { inclusive = true } } }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(96.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(stringResource(R.string.disclaimer_title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 24.dp))
            Text(stringResource(R.string.disclaimer_body), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            MGFilledButton(text = stringResource(R.string.disclaimer_accept), onClick = vm::accept, modifier = Modifier.padding(top = 32.dp))
            MGTextButton(text = stringResource(R.string.disclaimer_decline), onClick = { (navController.context as? android.app.Activity)?.finish() }, modifier = Modifier.padding(top = 8.dp))
        }
    }
}