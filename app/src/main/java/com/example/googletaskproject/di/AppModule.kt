package com.example.googletaskproject.di

import android.content.Context
import androidx.room.Room
import com.example.googletaskproject.data.local.db.TaskDatabase
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.utils.Const.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, TaskDatabase::class.java, DATABASE_NAME
    ).build()
    @Provides
    @Singleton
    fun provideDao(db: TaskDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideEntity() = TaskItem()
}