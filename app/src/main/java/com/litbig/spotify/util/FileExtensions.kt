package com.litbig.spotify.util

import java.io.File

object FileExtensions {
    @JvmStatic
    fun File.scanForMusicFiles(): List<File> {
        val musicFiles = mutableListOf<File>()
        val supportedExtensions = arrayOf("mp3")

        listFiles()?.forEach { file ->
            if (file.isDirectory && !file.name.startsWith("_") && !file.name.startsWith(".")) {
                musicFiles.addAll(file.scanForMusicFiles())
            } else if (supportedExtensions.any { file.name.endsWith(it, ignoreCase = true) }) {
                musicFiles.add(file)
            }
        }
        return musicFiles
    }
}