package com.example.monedaconvertor.data

import com.example.monedaconvertor.data.model.RemoteResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RetrofitService {
    //@GET("v6/c7783f7e2d6a7ad55ea925db/latest/BOB")
    @GET("v6/{apiKey}/latest/{currency}")
    suspend fun TodasMonedas(
        @Path("apiKey") apiKey: String,
        @Path("currency") baseCurrency: String
    ): RemoteResult
    //suspend fun TodasMonedas(
        //@Query("access_key") access_key: String,
        //@Query("pais") pais: String
    //): RemoteResult

    object RetrofitServiceFactory {
        fun makeRetrofitService(): RetrofitService {
            return Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService::class.java)
        }
    }
}