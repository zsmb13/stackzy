package com.theapache64.stackzy.ui.feature

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.theapache64.cyclone.core.Activity
import com.theapache64.cyclone.core.Intent
import com.theapache64.stackzy.App
import com.theapache64.stackzy.ui.navigation.NavHostComponent
import com.theapache64.stackzy.ui.theme.R
import com.theapache64.stackzy.ui.theme.StackzyTheme
import java.awt.Taskbar
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import androidx.compose.ui.window.Window as setContent


class MainActivity : Activity() {
    companion object {
        fun getStartIntent(): Intent {
            return Intent(MainActivity::class).apply {
                // data goes here
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            /*
             *TODO : Temp fix for https://github.com/theapache64/stackzy/issues/72
             *  Should be updated once resolved :
             */
            Taskbar.getTaskbar().iconImage = getAppIcon()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        }

        val lifecycle = LifecycleRegistry()
        val root = NavHostComponent(DefaultComponentContext(lifecycle))

        application {
            setContent(
                onCloseRequest = ::exitApplication,
                title = "${App.appArgs.appName} (${App.appArgs.version} - hacky decompile all edition)",
                icon = painterResource(R.drawables.appIcon),
                state = rememberWindowState(
                    width = 1224.dp,
                    height = 800.dp
                ),
            ) {
                StackzyTheme {
                    // Igniting navigation
                    root.render()
                }
            }
        }
    }
}


/**
 * To get app icon for toolbar and system tray
 */
private fun getAppIcon(): BufferedImage {

    // Retrieving image
    val resourceFile = MainActivity::class.java.classLoader.getResourceAsStream(R.drawables.appIcon)
    val imageInput = ImageIO.read(resourceFile)

    val newImage = BufferedImage(
        imageInput.width,
        imageInput.height,
        BufferedImage.TYPE_INT_ARGB
    )

    // Drawing
    val canvas = newImage.createGraphics()
    canvas.drawImage(imageInput, 0, 0, null)
    canvas.dispose()

    return newImage
}
