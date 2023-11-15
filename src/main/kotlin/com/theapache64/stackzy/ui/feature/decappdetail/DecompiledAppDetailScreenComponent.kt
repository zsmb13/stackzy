package com.theapache64.stackzy.ui.feature.decappdetail

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

class DecompiledAppDetailScreenComponent(
    appComponent: AppComponent,
    componentContext: ComponentContext,
    private val analysisReport: AnalysisReportWrapper?,
    private val apkSource: ApkSource<AndroidDeviceWrapper, Account>,
    val onLibrarySelected: (LibraryWrapper) -> Unit,
    private val onBackClicked: () -> Unit,
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var appDetailViewModel: DecompiledAppDetailViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(appDetailViewModel) {
            appDetailViewModel.init(
                scope = scope,
                apkSource = apkSource,
                analysisReport = analysisReport,
            )
        }

        DecompiledAppDetailScreen(
            viewModel = appDetailViewModel,
            onLibrarySelected = onLibrarySelected,
            onBackClicked = onBackClicked,
        )
    }
}
