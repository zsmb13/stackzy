package com.theapache64.stackzy.ui.feature.decappdetail

import com.github.theapache64.gpa.model.Account
import com.theapache64.stackzy.data.local.AnalysisReport
import com.theapache64.stackzy.data.remote.Config
import com.theapache64.stackzy.data.repo.*
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.AndroidDeviceWrapper
import com.theapache64.stackzy.util.ApkSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.inject.Inject

class DecompiledAppDetailViewModel @Inject constructor( ) {
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var apkSource: ApkSource<AndroidDeviceWrapper, Account>

    private val _analysisReport = MutableStateFlow<AnalysisReportWrapper?>(null)
    val analysisReport: StateFlow<AnalysisReportWrapper?> = _analysisReport

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    fun init(
        scope: CoroutineScope,
        apkSource: ApkSource<AndroidDeviceWrapper, Account>,
        analysisReport: AnalysisReportWrapper?,
    ) {
        this.viewModelScope = scope
        this.apkSource = apkSource
        _analysisReport.value = analysisReport
    }

    fun onTabClicked(index: Int) {
        _selectedTabIndex.value = index
    }

    fun onPlayStoreIconClicked() {
        _analysisReport.value?.let { report ->
            val playStoreUrl = URI("https://play.google.com/store/apps/details?id=${report.packageName}")
            Desktop.getDesktop().browse(playStoreUrl)
        }
    }
}
