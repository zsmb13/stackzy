package com.theapache64.stackzy.ui.feature.decall

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theapache64.stackzy.data.util.calladapter.flow.Resource
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.ui.common.CustomScaffold
import com.theapache64.stackzy.ui.common.ErrorSnackBar
import com.theapache64.stackzy.ui.common.Selectable
import com.theapache64.stackzy.ui.common.loading.LoadingAnimation


@Composable
fun DecompileAllScreen(
    viewModel: DecompileAllViewModel,
    onBackClicked: () -> Unit,
    onLibraryClicked: (LibraryWrapper, List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>) -> Unit,
    onAppSelected: (AnalysisReportWrapper?) -> Unit,
) {
    val librariesResp by viewModel.libsResp.collectAsState()
    val analysisResults by viewModel.appAnalysisResults.collectAsState()
    val subTitle by viewModel.subTitle.collectAsState()

    val doneCount by viewModel.doneCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()

    CustomScaffold(
        title = "Explore all decompiled apps",
        subTitle = subTitle,
        onBackClicked = onBackClicked,
        bottomGradient = true,
        topRightSlot = {
            Box(contentAlignment = Alignment.CenterEnd) {
                if (totalCount != 0) {
                    val progress by animateFloatAsState(doneCount / totalCount.toFloat(), animationSpec = tween(1500))
                    if (progress < 1f) {
                        LinearProgressIndicator(progress = progress, Modifier.padding(start = 16.dp).fillMaxSize())
                    }
                }
                val inProgressApps by viewModel.inProgress.collectAsState()
                Column(horizontalAlignment = Alignment.End) {
                    inProgressApps.forEach { packageName ->
                        Text(
                            text = packageName,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    ) {
        val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tabs
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { viewModel.onTabClicked(0) },
                    text = { Text("Libraries") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { viewModel.onTabClicked(1) },
                    text = { Text("Apps") }
                )
            }

            if (selectedTabIndex == 0) {
                // Libraries Tab
                Libraries(librariesResp, analysisResults, onLibraryClicked)
            } else {
                // More Info tab
                Apps(analysisResults.map { it.first }, onAppSelected = { androidAppWrapper ->
                    onAppSelected(analysisResults.first { it.first == androidAppWrapper }.second)
                })
            }
        }
    }
}

@Composable
private fun Libraries(
    librariesResp: Resource<List<LibraryWrapper>>?,
    analysisResults: List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>,
    onLibraryClicked: (LibraryWrapper, List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>) -> Unit,
) {
    when (librariesResp) {
        is Resource.Loading -> {
            LoadingAnimation("", funFacts = null)
        }

        is Resource.Error -> {
            Box {
                ErrorSnackBar(
                    librariesResp.errorData
                )
            }
        }

        is Resource.Success -> {
            val libraries = librariesResp.data

            Column {
                if (libraries.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4)
                    ) {
                        items(libraries) { library ->
                            Column {
                                // GridItem
                                Selectable(
                                    modifier = Modifier.fillMaxWidth(),
                                    data = library,
                                    onSelected = { library ->
                                        onLibraryClicked(
                                            library,
                                            analysisResults,
                                        )
                                    }
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )
                            }
                        }
                    }
                }
            }

        }

        null -> {
            LoadingAnimation("Preparing apps...", funFacts = null)
        }
    }
}

@Composable
private fun Apps(apps: List<AndroidAppWrapper>, onAppSelected: (AndroidAppWrapper) -> Unit) {
    if (apps.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {
            items(items = apps) { app ->
                Column {
                    Selectable(
                        data = app,
                        onSelected = onAppSelected
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
        }

    } else {
        LoadingAnimation("", funFacts = null)
    }
}
