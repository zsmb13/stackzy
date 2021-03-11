package com.theapache64.stackzy.ui.feature.appdetail

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.theapache64.gpa.model.Account
import com.theapache64.stackzy.data.local.AndroidApp
import com.theapache64.stackzy.data.local.AndroidDevice
import com.theapache64.stackzy.data.remote.Library
import com.theapache64.stackzy.di.AppComponent
import com.theapache64.stackzy.ui.navigation.Component
import com.theapache64.stackzy.util.Either
import javax.inject.Inject

class AppDetailScreenComponent(
    appComponent: AppComponent,
    componentContext: ComponentContext,
    selectedApp: AndroidApp,
    source: Either<AndroidDevice, Account>,
    val onLibrarySelected: (Library) -> Unit,
    private val onBackClicked: () -> Unit
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var appDetailViewModel: AppDetailViewModel

    init {

        appComponent.inject(this)

        appDetailViewModel.init(
            source = source,
            androidApp = selectedApp
        )
    }

    @Composable
    override fun render() {
        AppDetailScreen(
            appDetailViewModel = appDetailViewModel,
            onLibrarySelected = onLibrarySelected,
            onBackClicked = onBackClicked
        )
    }
}
