package com.theapache64.stackzy.data.local

import com.malinskiy.adam.request.pkg.Package

interface AndroidAppDefinition {
    val appPackage: Package
    val isSystemApp: Boolean
    val versionCode: Int?
    val versionName: String?
    val appTitle: String?
    val imageUrl: String?
    val appSize: String?
}

data class AndroidApp(
    override val appPackage: Package,
    override val isSystemApp: Boolean,
    override val versionCode: Int? = null,
    override val versionName: String? = null,
    override val appTitle: String? = null,
    override val imageUrl: String? = null,
    override val appSize: String? = null,
) : AndroidAppDefinition