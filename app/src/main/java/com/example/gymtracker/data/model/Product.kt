package com.example.gymtracker.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the product details we care about from the Open Food Facts API.
 * The @Serializable annotation allows the Kotlinx Serialization library to automatically
 * convert JSON into an instance of this class.
 */
@Serializable
data class Product(
    // The @SerialName annotation is used when the JSON key name is different from
    // the variable name, or if it contains characters not allowed in Kotlin variable names (like '-').
    @SerialName("product_name_en")
    val name: String? = "No name found",

    @SerialName("image_front_url")
    val imageUrl: String? = null,

    val brands: String? = "Unknown brand",

    // This property holds a nested object for nutrition facts.
    val nutriments: Nutriments? = null
)

/**
 * Represents the nested 'nutriments' object within the product data.
 * We only define the fields we are interested in.
 */
@Serializable
data class Nutriments(
    @SerialName("energy-kcal_100g")
    val energyKcalPer100g: Double? = 0.0,

    @SerialName("sugars_100g")
    val sugarsPer100g: Double? = 0.0,

    @SerialName("salt_100g")
    val saltPer100g: Double? = 0.0,

    @SerialName("proteins_100g")
    val proteinsPer100g: Double? = 0.0,

    @SerialName("fat_100g")
    val fatPer100g: Double? = 0.0
)

