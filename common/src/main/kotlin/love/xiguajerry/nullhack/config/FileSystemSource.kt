package love.xiguajerry.nullhack.config

import love.xiguajerry.nullhack.config.filesystem.AbstractFile
import love.xiguajerry.nullhack.config.filesystem.DefaultFileSystem
import love.xiguajerry.nullhack.config.filesystem.Path
import love.xiguajerry.nullhack.config.filesystem.ZipFileSystem
import org.apache.commons.compress.archivers.zip.ZipFile

sealed interface FileSystemSource {
    val root: AbstractFile

    data object DefaultDiskSource : FileSystemSource {
        override val root: AbstractFile get() = DefaultFileSystem.openFile(Path("."))
    }

    class ZipFileSource(val zipFile: ZipFile) : FileSystemSource {
        private val fs = ZipFileSystem(zipFile)
        override val root: AbstractFile
            get() = fs.openFile(Path("."))
    }
}