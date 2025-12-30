package com.apptolast.greenhouse.admin.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin dependency injection framework.
 * Call this function from each platform's entry point before accessing any dependencies.
 *
 * @param appDeclaration Optional platform-specific Koin configuration (e.g., androidContext for Android)
 */
fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            dataModule,
            domainModule,
            presentationModule,
            platformModule()
        )
    }
}
