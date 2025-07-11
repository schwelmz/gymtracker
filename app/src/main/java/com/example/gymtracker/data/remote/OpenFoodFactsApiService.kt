package com.example.gymtracker.data.remote

import com.example.gymtracker.data.model.OffApiResponse


import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApiService {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): OffApiResponse
}