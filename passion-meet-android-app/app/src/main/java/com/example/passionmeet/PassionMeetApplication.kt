package com.example.passionmeet

import android.app.Application
import com.example.passionmeet.di.databaseModule
import com.example.passionmeet.di.networkModule
import com.example.passionmeet.di.repositoryModule
import com.example.passionmeet.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PassionMeetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@PassionMeetApplication)
            modules(listOf(
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelModule
            ))
        }
    }
} 