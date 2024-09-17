package service

import model.CartItem
import model.Offer
import model.Product
import java.math.BigDecimal
import java.math.RoundingMode

class ReceiptGeneratorService {
    private val offerService = OfferService()

    fun generate(
        items: List<CartItem>,
        offerMap: Map<Product, Offer?>,
        taxCalculator: TaxCalculatorService
    ): String {
        val sb = StringBuilder()
        sb.append("===== Receipt =====\n")

        var totalBeforeTax = 0.0
        var totalAfterDiscount = 0.0

        for (item in items) {
            val (product, quantity) = item.run { product to quantity }
            val basePrice = product.price * quantity
            val discountedPrice = applyOfferAndCalculateDiscountedPrice(quantity, product.price, offerMap[product])

            sb.append("${product.name} x$quantity\n")
                .append("  Base price: $basePrice\n")
                .append("  Price after discount: $discountedPrice\n\n")

            totalBeforeTax += basePrice
            totalAfterDiscount += discountedPrice
        }

        val totalTax = taxCalculator.calculateTax(totalAfterDiscount)
        val totalWithTax = (totalAfterDiscount + totalTax).roundToNearestDecimalPlace()

        sb.append("====================\n")
            .append("Total before tax: $totalBeforeTax\n")
            .append("Total after discounts: $totalAfterDiscount\n")
            .append("Tax: ${totalTax.roundToNearestDecimalPlace()}\n")
            .append("Total price (with taxes): $totalWithTax\n")

        return sb.toString()
    }

    private fun applyOfferAndCalculateDiscountedPrice(
        quantity: Int,
        unitPrice: Double,
        offer: Offer?
    ): Double {
        return offerService.calculatePriceAfterOffer(quantity, unitPrice, offer)
    }
}

private fun Double.roundToNearestDecimalPlace(): Double {
    return BigDecimal(this).setScale(1, RoundingMode.HALF_UP).toDouble()
}
