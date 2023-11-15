package com.theapache64.stackzy.ui.feature.declibdetail

import com.github.theapache64.gpa.model.Account
import com.theapache64.stackzy.data.util.calladapter.flow.Resource
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.AndroidDeviceWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.util.ApkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DecompiledLibraryDetailViewModel @Inject constructor() {
    private lateinit var analysisResults: List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>
    private lateinit var libWrapper: LibraryWrapper
    private lateinit var apkSource: ApkSource<AndroidDeviceWrapper, Account>
    private lateinit var viewModelScope: CoroutineScope

    private val _apps = MutableStateFlow<Resource<List<AndroidAppWrapper>>?>(null)
    val apps = _apps.asStateFlow()

    private val _pageTitle = MutableStateFlow<String>("")
    val pageTitle = _pageTitle.asStateFlow()

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword = _searchKeyword.asStateFlow()

    private lateinit var onAppSelected: (ApkSource<AndroidDeviceWrapper, Account>, AnalysisReportWrapper?) -> Unit
    private lateinit var onLogInNeeded: (shouldGoToPlayStore: Boolean) -> Unit

    fun init(
        viewModelScope: CoroutineScope,
        libWrapper: LibraryWrapper,
        apkSource: ApkSource<AndroidDeviceWrapper, Account>,
        analysisResults: List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>,
        onAppSelected: (ApkSource<AndroidDeviceWrapper, Account>, AnalysisReportWrapper?) -> Unit,
        onLogInNeeded: (shouldGoToPlayStore: Boolean) -> Unit
    ) {
        this.viewModelScope = viewModelScope
        this.libWrapper = libWrapper
        this.apkSource = apkSource
        this.onAppSelected = onAppSelected
        this.onLogInNeeded = onLogInNeeded

        this.analysisResults = analysisResults

        this._pageTitle.value = libWrapper.name
    }

    fun loadApps() {
        viewModelScope.launch {
            _apps.value = Resource.Success(
                analysisResults.filter { (app, report) ->
                    report != null && report.libraryWrappers.any { it.packageName == libWrapper.packageName }
                }.map { it.first }
            )
        }
    }

    fun onAppClicked(appWrapper: AndroidAppWrapper) {
        val analysisReport = analysisResults.first { it.first == appWrapper }.second
        onAppSelected(apkSource, analysisReport)
    }
}
