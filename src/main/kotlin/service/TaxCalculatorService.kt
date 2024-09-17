package service

import kotlin.math.round

class TaxCalculatorService(private val taxRate: Double = 0.10) {
    fun calculateTax(basePrice: Double): Double {
        val taxAmount = basePrice * taxRate
        return round(taxAmount * 100) / 100
    }
}