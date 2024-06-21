package com.issever.issevercore.di

import com.issever.core.data.remote.CoreNetwork.provideRetrofit
import com.issever.issevercore.data.service.FactService
import com.issever.issevercore.utils.Constants.FACTS_BASE_URL
import com.issever.issevercore.utils.Constants.CAT_FACTS_RETROFIT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named(CAT_FACTS_RETROFIT)
    fun provideFactRetrofit(): Retrofit = provideRetrofit(FACTS_BASE_URL)

    @Provides
    @Singleton
    fun provideFactService(@Named(CAT_FACTS_RETROFIT) retrofit: Retrofit): FactService {
        return retrofit.create(FactService::class.java)
    }

}