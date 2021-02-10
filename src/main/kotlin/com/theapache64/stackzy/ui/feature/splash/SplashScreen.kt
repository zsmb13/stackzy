package com.theapache64.stackzy.ui.feature.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theapache64.stackzy.ui.common.LoadingText
import com.theapache64.stackzy.ui.common.Logo
import com.theapache64.stackzy.util.R

@ExperimentalFoundationApi
@Composable
fun SplashScreen(splashViewModel: SplashViewModel) {

    val isSyncFinished by splashViewModel.isSyncFinished.collectAsState()
    val syncFailedReason by splashViewModel.isSyncFailed.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Logo(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )

        if (isSyncFinished.not()) {
            LoadingText(
                message = "Syncing...",
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .align(Alignment.BottomCenter)

            )
        }

        if (syncFailedReason != null) {
            Snackbar(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomCenter),
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                action = {
                    TextButton(
                        onClick = {
                            splashViewModel.onRetryClicked()
                        }
                    ) {
                        Text(R.string.all_action_retry)
                    }
                }
            ) {
                Text(text = syncFailedReason ?: R.string.all_error_unknown)
            }
        }
    }
}
