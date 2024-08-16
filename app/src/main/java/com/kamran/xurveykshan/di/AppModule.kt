package com.kamran.xurveykshan.di

import android.content.Context
import androidx.room.Room
import com.kamran.xurveykshan.repository.DataRepository
import com.kamran.xurveykshan.data.AppDatabase
import com.kamran.xurveykshan.data.DataDao
import com.kamran.xurveykshan.viewModels.DataViewModel
import com.kamran.xurveykshan.viewModels.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { provideAppDatabase(get()) }

    single<DataDao> { get<AppDatabase>().dao() }

    single { DataRepository(get()) }

    viewModel { DataViewModel(get()) }

    viewModel{ MainViewModel(get(), get())}

}

private fun provideAppDatabase(applicationContext: Context): AppDatabase {
    return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "contacts_db")
        .fallbackToDestructiveMigration(false)
        .build()
}