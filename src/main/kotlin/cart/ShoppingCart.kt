package cart

import model.CartItem
import model.Offer
import model.Product
import service.OfferService
import service.ReceiptGeneratorService
import service.TaxCalculatorService

class ShoppingCart {
    private val items = mutableListOf<CartItem>()
    private val taxCalculatorService = TaxCalculatorService()
    private val offerMap = mutableMapOf<Product, Offer?>()
    private val offerService = OfferService()

    fun addItem(product: Product, quantity: Int = 1) {
        items.find { it.product == product }
            ?.apply { this.quantity += quantity }
            ?: items.add(CartItem(product, quantity))
    }

    fun removeItem(product: Product, quantity: Int = 1) {
        items.find { it.product == product }?.let { item ->
            item.quantity = (item.quantity - quantity).coerceAtLeast(0)
            if (item.quantity == 0) items.remove(item)
        }
    }

    fun applyOffer(product: Product, offer: Offer?) {
        offerMap[product] = offer
    }

    fun calculateTotalPrice(): Double {
        val basePrice = items.sumOf { it.product.price * it.quantity }

        val totalTax = taxCalculatorService.calculateTax(basePrice)

        val discountedPrice = items.sumOf { item ->
            val offer = offerMap[item.product]
            offerService.buildOfferPrice(item.quantity, item.product.price, offer)
        }

        return discountedPrice + totalTax
    }

    fun buildReceipt(): String {
        val receiptGenerator = ReceiptGeneratorService()
        return receiptGenerator.generate(items, offerMap, taxCalculatorService)
    }
}