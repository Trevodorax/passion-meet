package com.example.passionmeet.di

import androidx.lifecycle.LifecycleOwner
import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.LoginViewModel
import com.example.passionmeet.viewmodel.PassionViewModel
import com.example.passionmeet.viewmodel.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { 
        PassionViewModel(
            passionRepository = get(),
            context = get()
        )
    }
    
    viewModel { 
        GroupViewModel(
            groupRepository = get(),
            context = get()
        )
    }
    
    viewModel { 
        LoginViewModel(
            loginRepository = get(),
            context = get()
        )
    }
    
    viewModel { 
        SignupViewModel(
            signupRepository = get(),
            context = get()
        )
    }
} 