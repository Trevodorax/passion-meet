package com.example.passionmeet.di

import com.example.passionmeet.data.local.PassionMeetDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { PassionMeetDatabase.getDatabase(androidContext()) }
    single { get<PassionMeetDatabase>().passionDao() }
    single { get<PassionMeetDatabase>().groupDao() }
} 