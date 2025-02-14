package com.example.passionmeet.di

import com.example.passionmeet.data.remote.api.MessageApi
import com.example.passionmeet.network.RetrofitClient
import com.example.passionmeet.network.services.EncounterService
import com.example.passionmeet.network.services.GroupService
import com.example.passionmeet.network.services.LoginService
import com.example.passionmeet.network.services.PassionService
import com.example.passionmeet.network.services.SignupService
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single { RetrofitClient.instance }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(PassionService::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(GroupService::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(LoginService::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(SignupService::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(EncounterService::class.java)
    }

    single {
        val retrofit: Retrofit = get()
        retrofit.create(MessageApi::class.java)
    }
}