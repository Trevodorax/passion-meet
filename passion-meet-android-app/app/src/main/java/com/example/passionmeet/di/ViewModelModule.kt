package com.example.passionmeet.di

import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.LoginViewModel
import com.example.passionmeet.viewmodel.PassionViewModel
import com.example.passionmeet.viewmodel.SignupViewModel
import com.example.passionmeet.viewmodel.EncounterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.passionmeet.ui.chat.GroupChatViewModel
import com.example.passionmeet.viewmodel.ActivityViewModel

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
    
    viewModel {
        EncounterViewModel(
            encounterRepository = get(),
            context = get()
        )
    }

    viewModel {
        GroupChatViewModel(
            messageRepository = get(),
            context = get()
        )
    }

    viewModel {
        ActivityViewModel(
            activityRepository = get(),
            context = get()
        )
    }

}