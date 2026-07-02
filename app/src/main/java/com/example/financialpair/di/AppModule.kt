package com.example.financialpair.di

import com.example.financialpair.data.AppDatabase
import com.example.financialpair.data.repository.CategoryRepository
import com.example.financialpair.data.repository.MovementRepository
import com.example.financialpair.data.repository.TopicRepository
import com.example.financialpair.ui.screens.movements.MovementsScreenViewModel
import com.example.financialpair.ui.screens.topics.TopicsScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().movementDao() }
    single { get<AppDatabase>().topicDao() }
    single { get<AppDatabase>().categoryDao() }
    single { MovementRepository(get()) }
    single { TopicRepository(get()) }
    single { CategoryRepository(get()) }
    viewModel { MovementsScreenViewModel(get()) }
    viewModel { TopicsScreenViewModel(get(), get()) }
}