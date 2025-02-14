package com.example.passionmeet.di

import androidx.lifecycle.LifecycleOwner
import com.example.passionmeet.viewmodel.GroupViewModel
import com.example.passionmeet.viewmodel.LoginViewModel
import com.example.passionmeet.viewmodel.PassionViewModel
import com.example.passionmeet.viewmodel.SignupViewModel
import com.example.passionmeet.viewmodel.EncounterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import androidx.lifecycle.ViewModel
import com.example.passionmeet.ui.chat.GroupChatViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap

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
}

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(GroupChatViewModel::class)
    abstract fun bindGroupChatViewModel(viewModel: GroupChatViewModel): ViewModel
} 