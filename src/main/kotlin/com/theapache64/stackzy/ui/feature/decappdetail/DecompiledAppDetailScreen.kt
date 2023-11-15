package com.theapache64.stackzy.ui.feature.decappdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.ui.common.Badge
import com.theapache64.stackzy.ui.common.CustomScaffold
import com.theapache64.stackzy.ui.common.FullScreenError
import com.theapache64.stackzy.ui.common.loading.LoadingAnimation
import com.theapache64.stackzy.ui.feature.appdetail.AppDetailViewModel
import com.theapache64.stackzy.ui.feature.appdetail.Libraries
import com.theapache64.stackzy.ui.feature.appdetail.MoreInfo
import com.theapache64.stackzy.util.R


@Composable
fun DecompiledAppDetailScreen(
    viewModel: DecompiledAppDetailViewModel,
    onBackClicked: () -> Unit,
    onLibrarySelected: (LibraryWrapper) -> Unit,
) {
    val report by viewModel.analysisReport.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

    val title = report?.appName ?: R.string.app_detail_title

    CustomScaffold(
        title = title,
        subTitle = report?.platform?.name,
        onBackClicked = onBackClicked,
        bottomGradient = true,
        topRightSlot = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                report?.let {

                    // Badge
                    Badge("APK SIZE: ${it.apkSizeInMb} MB")

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    // Launch app in play-store icon
                    PlayStoreIcon {
                        viewModel.onPlayStoreIconClicked()
                    }
                }
            }
        }
    ) {
        val roReport = report

        if (roReport == null) {
            FullScreenError("Can't load details", "App probably failed to be decompiled")
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        AppDetailViewModel.TABS.forEachIndexed { index, title ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = {
                                    viewModel.onTabClicked(index)
                                },
                                text = { Text(text = title) }
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> Libraries(roReport, onLibrarySelected)
                        1 -> MoreInfo(roReport)
                    }
                }
            }
        }
    }
}

private val iconSize = 24.dp

@Composable
private fun PlayStoreIcon(onClicked: () -> Unit) {
    IconButton(
        onClick = onClicked
    ) {
        Icon(
            painter = painterResource("drawables/playstore.svg"),
            contentDescription = "open play store",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(iconSize)
        )
    }
}
