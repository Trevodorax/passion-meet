package com.example.passionmeet.di

import com.example.passionmeet.repositories.GroupRepository
import com.example.passionmeet.repositories.LoginRepository
import com.example.passionmeet.repositories.PassionRepository
import com.example.passionmeet.repositories.SignupRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { PassionRepository(androidContext(), get(), get()) }
    single { GroupRepository(androidContext(), get(), get()) }
    single { LoginRepository(androidContext()) }
    single { SignupRepository(androidContext()) }
} 