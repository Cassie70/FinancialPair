package com.example.financialpair.di

import com.example.financialpair.data.AppDatabase
import com.example.financialpair.data.repository.MovementRepository
import com.example.financialpair.ui.screens.MovementsScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().movementDao() }
    single { get<AppDatabase>().topicDao() }
    single { MovementRepository(get()) }
    viewModel { MovementsScreenViewModel(get()) }
}