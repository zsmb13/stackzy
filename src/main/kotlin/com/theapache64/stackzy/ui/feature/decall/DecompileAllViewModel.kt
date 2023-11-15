package com.theapache64.stackzy.ui.feature.decall

import com.github.theapache64.gpa.model.Account
import com.theapache64.stackzy.data.util.calladapter.flow.Resource
import com.theapache64.stackzy.model.AnalysisReportWrapper
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.AndroidDeviceWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.ui.feature.appdetail.AppDetailViewModel
import com.theapache64.stackzy.util.ApkSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject
import javax.inject.Provider

class DecompileAllViewModel @Inject constructor(
    private val appDetailViewModelProvider: Provider<AppDetailViewModel>,
) {
    private lateinit var viewModelScope: CoroutineScope

    val doneCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalCount: MutableStateFlow<Int> = MutableStateFlow(0)

    val errorCount: MutableStateFlow<Int> = MutableStateFlow(0)
    
    private val _subTitle = MutableStateFlow("")
    val subTitle: StateFlow<String> = _subTitle

    private val _libsResp = MutableStateFlow<Resource<List<LibraryWrapper>>?>(null)
    val libsResp: StateFlow<Resource<List<LibraryWrapper>>?> = _libsResp

    val inProgress = MutableStateFlow<List<String>>(emptyList())

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    fun init(
        scope: CoroutineScope,
    ) {
        this.viewModelScope = scope
    }

    val appAnalysisResults = MutableStateFlow<List<Pair<AndroidAppWrapper, AnalysisReportWrapper?>>>(emptyList())

    fun loadLibraries(
        apkSource: ApkSource<AndroidDeviceWrapper, Account>,
        androidAppWrappers: List<AndroidAppWrapper>
    ) {
        viewModelScope.launch {
            val apps = androidAppWrappers

            totalCount.update { apps.size }
            
            val dotJob = launch {
                while (true) {
                    _subTitle.value = "[${doneCount.value}/${apps.size}] Analyzing."
                    delay(500)
                    _subTitle.value = "[${doneCount.value}/${apps.size}] Analyzing.."
                    delay(500)
                    _subTitle.value = "[${doneCount.value}/${apps.size}] Analyzing..."
                    delay(500)
                }
            }

            val libWrappers = MutableStateFlow<List<LibraryWrapper>>(emptyList())
            launch {
                libWrappers.collect { libs ->
                    if (libs.isNotEmpty()) {
                        _libsResp.value = Resource.Success(libs)
                    }
                }
            }

            _libsResp.value = Resource.Loading()

            val semaphore = Semaphore(5)

            val start = System.currentTimeMillis()
            apps.map { app ->
                launch {
                    semaphore.withPermit {
                        val appPackage = app.appPackage.name
                        inProgress.update { it + appPackage }

                        println("Starting decompile for $appPackage")

                        val appDetailViewModel = appDetailViewModelProvider.get()

                        appDetailViewModel.init(this, apkSource, app)
                        try {
                            appDetailViewModel.decompileViaAdb()
                        } catch (e: Exception) {
                            if (e is CancellationException) throw e
                            e.printStackTrace()

                            appAnalysisResults.update { results ->
                                results + (app to null)
                            }
                            inProgress.update { it - appPackage }
                            errorCount.update { it + 1 }
                            return@withPermit
                        }

                        val report = appDetailViewModel.analysisReport.first { it != null }!!

                        appAnalysisResults.update { results ->
                            val appWithProperTitle = app.copy(
                                androidApp = app.androidApp.copy(appTitle = report.appName)
                            )
                            results + (appWithProperTitle to report)
                        }
                        libWrappers.update { existing ->
                            (existing + report.libraryWrappers).distinctBy { it.packageName }.sortedBy { it.name }
                        }
                        doneCount.update { appAnalysisResults.value.size }

                        inProgress.update { it - appPackage }
                    }
                }
            }.joinAll()

            dotJob.cancel()
            
            _subTitle.value =
                "[${apps.size}/${apps.size}] Analyzed all apps in ${(System.currentTimeMillis() - start) / 1000f} seconds with ${errorCount.value} errors"
            doneCount.value = apps.size
        }
    }

    fun onTabClicked(index: Int) {
        _selectedTabIndex.value = index
    }
}
