package cz.kureii.raintext.modules

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import cz.kureii.raintext.model.EncryptionManager
import cz.kureii.raintext.services.SavePasswordWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideEncryptionManager(): EncryptionManager {
        return EncryptionManager()
    }

}
@InstallIn(SingletonComponent::class)
@EntryPoint
interface WorkerFactoryProvider {
    fun getWorkerFactory(): HiltWorkerFactory
}
