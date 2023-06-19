package com.semnazri.ocrcalculator.di

import com.semnazri.ocrcalculator.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{MainViewModel()}
}
