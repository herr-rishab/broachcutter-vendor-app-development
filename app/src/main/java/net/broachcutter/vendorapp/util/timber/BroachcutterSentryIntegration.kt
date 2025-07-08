package net.broachcutter.vendorapp.util.timber

import io.sentry.*
import io.sentry.protocol.SdkVersion
import timber.log.Timber
import java.io.Closeable

/**
 * Sentry integration for Timber.
 *
 * Modified version of [io.sentry.android.timber.SentryTimberIntegration].
 */
class BroachcutterSentryIntegration(
    private val minEventLevel: SentryLevel = SentryLevel.ERROR,
    private val minBreadcrumbLevel: SentryLevel = SentryLevel.INFO
) : Integration, Closeable {
    private lateinit var tree: BroachcutterSentryTimberTree
    private lateinit var logger: ILogger

    override fun register(hub: IHub, options: SentryOptions) {
        createSdkVersion(options)
        logger = options.logger

        tree = BroachcutterSentryTimberTree(hub, minEventLevel, minBreadcrumbLevel)
        Timber.plant(tree)

        logger.log(SentryLevel.DEBUG, "SentryTimberIntegration installed.")
    }

    override fun close() {
        if (this::tree.isInitialized) {
            Timber.uproot(tree)

            if (this::logger.isInitialized) {
                logger.log(SentryLevel.DEBUG, "SentryTimberIntegration removed.")
            }
        }
    }

    private fun createSdkVersion(options: SentryOptions): SdkVersion {
        var sdkVersion = options.sdkVersion

//        val name = SENTRY_TIMBER_SDK_NAME
        val name = "sentry.java.android.timber"
//        val version = VERSION_NAME
        val version = "6.0.0"
        sdkVersion = SdkVersion.updateSdkVersion(sdkVersion, name, version)

        sdkVersion.addPackage("maven:io.sentry:sentry-android-timber", version)

        return sdkVersion
    }
}
