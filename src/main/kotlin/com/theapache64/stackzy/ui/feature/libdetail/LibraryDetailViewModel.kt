package com.theapache64.stackzy.ui.feature.libdetail

import com.malinskiy.adam.request.pkg.Package
import com.theapache64.stackzy.data.local.AndroidApp
import com.theapache64.stackzy.data.repo.ResultsRepo
import com.theapache64.stackzy.data.util.calladapter.flow.Resource
import com.theapache64.stackzy.model.AndroidAppWrapper
import com.theapache64.stackzy.model.LibraryWrapper
import com.theapache64.stackzy.util.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryDetailViewModel @Inject constructor(
    private val resultsRepo: ResultsRepo
) {
    private lateinit var libWrapper: LibraryWrapper
    private lateinit var viewModelScope: CoroutineScope

    private val _apps = MutableStateFlow<Resource<List<AndroidAppWrapper>>?>(null)
    val apps = _apps.asStateFlow()

    private val _pageTitle = MutableStateFlow<String>("")
    val pageTitle = _pageTitle.asStateFlow()

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword = _searchKeyword.asStateFlow()

    fun init(viewModelScope: CoroutineScope, libWrapper: LibraryWrapper) {
        this.viewModelScope = viewModelScope
        this.libWrapper = libWrapper

        this._pageTitle.value = libWrapper.name
    }

    fun loadApps() {
        viewModelScope.launch {
            resultsRepo.getResults(libWrapper.packageName).collect {
                when (it) {
                    is Resource.Loading -> {
                        _apps.value = Resource.Loading(R.string.lib_detail_loading)
                    }
                    is Resource.Success -> {
                        val apps = it.data
                            .distinctBy { result -> result.packageName }
                            .map { result ->
                                AndroidAppWrapper(
                                    AndroidApp(
                                        appPackage = Package(name = result.packageName),
                                        isSystemApp = false,
                                        versionCode = result.versionCode,
                                        versionName = result.versionName,
                                        appTitle = result.appName,
                                        imageUrl = result.logoImageUrl
                                    ),
                                    shouldUseVersionNameAsSubTitle = true
                                )
                            }
                        _apps.value = Resource.Success(apps)
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

}