package com.example.apidemo.di

import androidx.room.Room
import com.example.apidemo.data.local.db.QuotesDb
import com.example.apidemo.data.network.QuoteApi
import com.example.apidemo.util.Constants
import com.example.apidemo.ui.viewmodel.QuoteViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val databaseModule : Module = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            QuotesDb::class.java,
            "quotes.db",
        ).build()
    }
}

val networkingModule: Module = module {
    single {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(provideOkHttp())
            .build()
            .create(QuoteApi::class.java)
    }
}

fun provideOkHttp(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(provideLoggingInterceptor())
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

}

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
}

val viewModelModule = module {
    viewModel {
        QuoteViewModel(get())
    }
}

val appModule : List<Module> = listOf(
    networkingModule,
    databaseModule,
    viewModelModule
)