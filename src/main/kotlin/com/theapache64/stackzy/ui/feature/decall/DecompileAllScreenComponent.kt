package com.theapache64.stackzy.ui.feature.decall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.github.theapache64.gpa.model.Account
import com.theapache64.stackzy.di.AppComponent
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.AndroidDeviceWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.ui.navigation.Component
import com.theapache64.stackzy.util.ApkSource
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

class DecompileAllScreenComponent(
    componentContext: ComponentContext,
    appComponent: AppComponent,
    val onLibraryClicked: (LibraryWrapper, ApkSource<AndroidDeviceWrapper, Account>, List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>) -> Unit,
    val onAppSelected: (ApkSource<AndroidDeviceWrapper, Account>, AnalysisReportWrapper?) -> Unit,
    val onBackClicked: () -> Unit,
    val apkSource: ApkSource<AndroidDeviceWrapper, Account>,
    val androidAppWrappers: List<AndroidAppWrapper>,
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var decompileAllViewModel: DecompileAllViewModel

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {
        LaunchedEffect(decompileAllViewModel) {
            println("VM instance is $decompileAllViewModel")
            println("Scope is $scope")

            decompileAllViewModel.init(scope)

            if (decompileAllViewModel.libsResp.value == null) {
                decompileAllViewModel.loadLibraries(apkSource, androidAppWrappers)
            }
        }

        DecompileAllScreen(
            viewModel = decompileAllViewModel,
            onBackClicked = {
                println("Destroying scope")
                scope.cancel()
                onBackClicked()
            },
            onLibraryClicked = { libWrapper, analysisResults ->
                onLibraryClicked(libWrapper, apkSource, analysisResults)
            },
            onAppSelected = {
                onAppSelected(apkSource, it)
            },
        )
    }
}