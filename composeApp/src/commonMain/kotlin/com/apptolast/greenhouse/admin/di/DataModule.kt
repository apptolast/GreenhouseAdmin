package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.data.repository.AlertsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.ClientsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.DashboardRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.DevicesRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.GreenhousesRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.SectorsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.SettingsRepositoryImpl
import com.apptolast.greenhouse.admin.data.repository.UsersRepositoryImpl
import com.apptolast.greenhouse.admin.domain.repository.AlertsRepository
import com.apptolast.greenhouse.admin.domain.repository.ClientsRepository
import com.apptolast.greenhouse.admin.domain.repository.DashboardRepository
import com.apptolast.greenhouse.admin.domain.repository.DevicesRepository
import com.apptolast.greenhouse.admin.domain.repository.GreenhousesRepository
import com.apptolast.greenhouse.admin.domain.repository.SectorsRepository
import com.apptolast.greenhouse.admin.domain.repository.SettingsRepository
import com.apptolast.greenhouse.admin.domain.repository.UsersRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for data layer dependencies.
 * Contains HTTP clients, API services, and repository implementations.
 */
val dataModule = module {
    // HTTP Client (uncomment when API is ready)
    // single { createHttpClient() }

    // API Services (uncomment when API is ready)
    // singleOf(::DashboardApiServiceImpl) bind DashboardApiService::class

    // Repositories
    singleOf(::DashboardRepositoryImpl) bind DashboardRepository::class
    singleOf(::ClientsRepositoryImpl) bind ClientsRepository::class
    singleOf(::UsersRepositoryImpl) bind UsersRepository::class
    singleOf(::GreenhousesRepositoryImpl) bind GreenhousesRepository::class
    singleOf(::SectorsRepositoryImpl) bind SectorsRepository::class
    singleOf(::DevicesRepositoryImpl) bind DevicesRepository::class
    singleOf(::AlertsRepositoryImpl) bind AlertsRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}
