package model

data class Product(
    val name: String,
    val sku: String,  // Unique identifier for the product
    val price: Double,
    val category: Category,
    val brand: String
)
