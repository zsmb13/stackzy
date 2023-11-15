package com.theapache64.stackzy.ui.feature.declibdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.github.theapache64.gpa.model.Account
import com.theapache64.stackzy.di.AppComponent
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.AndroidDeviceWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.ui.navigation.Component
import com.theapache64.stackzy.util.ApkSource
import javax.inject.Inject

class DecompiledLibraryDetailScreenComponent(
    componentContext: ComponentContext,
    appComponent: AppComponent,
    val libraryWrapper: LibraryWrapper,
    val apkSource: ApkSource<AndroidDeviceWrapper, Account>,
    val analysisResults: List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>,
    val onAppClicked: (ApkSource<AndroidDeviceWrapper, Account>, AnalysisReportWrapper?) -> Unit,
    val onBackClicked: () -> Unit,
    val onLogInNeeded: (Boolean) -> Unit,
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var libDetailViewModel: DecompiledLibraryDetailViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(libDetailViewModel) {

            libDetailViewModel.init(scope, libraryWrapper, apkSource, analysisResults, onAppClicked, onLogInNeeded)

            if (libDetailViewModel.apps.value == null) {
                libDetailViewModel.loadApps()
            }
        }

        DecompiledLibraryDetailScreen(
            viewModel = libDetailViewModel,
            onBackClicked = onBackClicked
        )
    }
}