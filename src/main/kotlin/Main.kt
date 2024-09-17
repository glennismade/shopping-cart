import cart.ShoppingCart
import model.Category
import model.Offer
import model.OfferType
import model.Product

fun main() {
    val cornflakes = Product("Cornflakes", "SKU123", 3.0, Category.GROCERY, "Brand A")
    val laptop = Product("Laptop", "SKU456", 1000.0, Category.ELECTRONICS, "Brand B")

    val cart = ShoppingCart()
    cart.addItem(cornflakes, 3)
    cart.addItem(laptop, 1)

    val twoForOneOffer = Offer(OfferType.BUY_N_GET_M_FREE, 2, 1)
    val laptopDiscount = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 10.0)

    cart.applyOffer(cornflakes, twoForOneOffer)
    cart.applyOffer(laptop, laptopDiscount)

    println(cart.buildReceipt())
}



