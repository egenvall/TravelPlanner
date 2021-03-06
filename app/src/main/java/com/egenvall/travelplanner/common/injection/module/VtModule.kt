package com.egenvall.travelplanner.common.injection.module

import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.network.Repository
import com.egenvall.travelplanner.network.RestDataSource
import com.egenvall.travelplanner.network.VtService
import com.egenvall.travelplanner.util.ObjectAsListJsonAdapterFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
 class VtModule(private val app : TravelPlanner) {
    val baseUrl = "https://api.vasttrafik.se/bin/rest.exe/v2/"

    @Singleton
    @Provides
    internal  fun provideMoshi() : MoshiConverterFactory{
        val m = Moshi.Builder().add(ObjectAsListJsonAdapterFactory()).build()
        return MoshiConverterFactory.create(m)
    }
    @Singleton
    @Provides
    internal fun provideRetrofit(moshiConverterFactory: MoshiConverterFactory): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(moshiConverterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit
    }

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit): VtService {
        return retrofit.create(VtService::class.java)
    }

    /**

     * @param token empty AccessToken
     * *
     * @param service Västtrafik API endpoints
     * *
     * @return Instance that implements Repository, whom exposes methods that can be fetched from
     * * API
     */
    @Provides
    @Singleton
    fun provideRepository(service: VtService): Repository {
        return RestDataSource(service)
    }
}