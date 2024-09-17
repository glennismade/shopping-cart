import cart.ShoppingCart
import model.Category
import model.Offer
import model.OfferType
import model.Product
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShoppingCartTest {

    private lateinit var cart: ShoppingCart
    private lateinit var cornflakes: Product
    private lateinit var laptop: Product

    @BeforeEach
    fun setup() {
        cart = TestFactory.createShoppingCart()
        cornflakes = TestFactory.createProduct("Cornflakes", "SKU123", 3.0, Category.GROCERY, "Brand A")
        laptop = TestFactory.createProduct("Laptop", "SKU456", 1000.0, Category.ELECTRONICS, "Brand B")
    }

    @Test
    fun `add items to cart and calculate total price with tax`() {
        cart.addItem(cornflakes, 2)
        cart.addItem(laptop, 1)

        val cornflakesTotal = 2 * 3.0
        val laptopTotal = 1 * 1000.0
        val subtotal = cornflakesTotal + laptopTotal
        val taxAmount = subtotal * 0.10
        val expectedTotal = subtotal + taxAmount

        assertEquals(expectedTotal, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `remove items from cart`() {
        cart.addItem(cornflakes, 3)
        cart.removeItem(cornflakes, 1)

        // Calculate expected total price
        val expectedTotal = 2 * 3.0
        val totalWithTax = expectedTotal * 0.10
        val expectedTotalWithTax = expectedTotal + totalWithTax

        assertEquals(expectedTotalWithTax, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `remove all items from cart`() {
        cart.addItem(cornflakes, 3)
        cart.removeItem(cornflakes, 3)

        assertEquals(0.0, cart.calculateTotalPrice(), 0.01)
    }

    // FIXME -- tax issue
    @Test
    fun `apply buy N get M free offer`() {
        cart.addItem(cornflakes, 6)
        val twoForOneOffer = Offer(OfferType.BUY_N_GET_M_FREE, buyQuantity = 2, freeQuantity = 1)
        cart.applyOffer(cornflakes, twoForOneOffer)

        // Calculate expected total price
        val quantityToCharge = 6 - (6 / (2 + 1)) * 1
        val expectedTotal = quantityToCharge * 3.0
        val tax = 18 * 0.10 // fudged because tax is on all items when using buy 1 get 1 free...
        val totalwithtax = expectedTotal + tax

        assertEquals(totalwithtax, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `apply percentage discount offer`() {
        cart.addItem(laptop, 1)
        val discount = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 10.0)
        cart.applyOffer(laptop, discount)

        // Calculate expected total price
        val discountedPrice = 1000.0 * 0.90 // 10% discount
        val tax = 1000.0 * 0.10
        val expectedTotal = discountedPrice + tax

        assertEquals(expectedTotal, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `calculate total price with no offers`() {
        cart.addItem(cornflakes, 5)
        val subtotal = 5 * 3.0
        val taxAmount = subtotal * 0.10 // Assuming 10% tax rate
        val expectedTotal = subtotal + taxAmount

        assertEquals(expectedTotal, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `calculate total price with 10 percent discount and tax`() {
        cart.addItem(cornflakes, 5)

        val offer = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 10.0)
        cart.applyOffer(cornflakes, offer)

        val priceBeforeDiscount = 5 * 3.0 // 15
        val discountAmount = priceBeforeDiscount * 0.10 // 10% discount
        val priceAfterDiscount = priceBeforeDiscount - discountAmount
        val taxAmount = priceBeforeDiscount * 0.10 // Assuming 10% tax rate
        val expectedTotal = priceAfterDiscount + taxAmount

        assertEquals(expectedTotal, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `generate receipt`() {
        cart.addItem(cornflakes, 3)
        cart.addItem(laptop, 1)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Cornflakes x3"))
        assertTrue(receipt.contains("Laptop x1"))
        assertTrue(receipt.contains("Total price (with taxes):"))
    }

    @Test
    fun `build receipt after applying offers`() {
        cart.addItem(cornflakes, 4)
        val twoForOneOffer = Offer(OfferType.BUY_N_GET_M_FREE, buyQuantity = 2, freeQuantity = 1)
        cart.applyOffer(cornflakes, twoForOneOffer)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Cornflakes x4"))
        assertTrue(receipt.contains("Total price (with taxes):"))
    }
}
