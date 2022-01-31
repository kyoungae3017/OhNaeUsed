package com.kyoungae.ohnaejunggo.di

import android.content.Context
import android.content.SharedPreferences
import com.kyoungae.ohnaejunggo.util.SHARED_PREFERENCE_FILE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferenceModule {

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences{
        return context.getSharedPreferences(SHARED_PREFERENCE_FILE_NAME,Context.MODE_PRIVATE)
    }
}