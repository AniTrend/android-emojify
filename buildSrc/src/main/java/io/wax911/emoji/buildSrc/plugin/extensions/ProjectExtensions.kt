package io.wax911.emoji.buildSrc.plugin.extensions

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import io.wax911.emoji.buildSrc.module.Modules
import io.wax911.emoji.buildSrc.plugin.components.PropertiesReader
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.LibrariesForSampleLibs
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

fun Project.isSampleModule() =
    name == Modules.App.Sample.id

fun Project.isLibraryModule() =
    name != Modules.App.Sample.id

internal val Project.libs: LibrariesForLibs
    get() = extensions.getByType<LibrariesForLibs>()

internal val Project.sampleLibs: LibrariesForSampleLibs
    get() = extensions.getByType<LibrariesForSampleLibs>()

internal val Project.props: PropertiesReader
    get() = PropertiesReader(this)

internal fun Project.baseExtension() =
    extensions.getByType<BaseExtension>()

internal fun Project.baseAppExtension() =
    extensions.getByType<BaseAppModuleExtension>()

internal fun Project.publishingExtension() =
    extensions.getByType<PublishingExtension>()

internal fun Project.spotlessExtension() =
    extensions.getByType<SpotlessExtension>()
