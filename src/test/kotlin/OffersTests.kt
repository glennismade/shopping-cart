import cart.ShoppingCart
import model.Category
import model.Offer
import model.OfferType
import model.Product
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import service.TaxCalculatorService


class OffersTests {

    private lateinit var cart: ShoppingCart
    private lateinit var product: Product
    private val taxCalculatorService = TaxCalculatorService()

    @BeforeEach
    fun setup() {
        cart = TestFactory.createShoppingCart()
        product = TestFactory.createProduct("Sample model.Product", "SKU123", 10.0, Category.GROCERY, "Brand X")
    }

    @Test
    fun `buy N get M free offer logic with tax`() {
        cart.addItem(product, 5)
        val offer = Offer(OfferType.BUY_N_GET_M_FREE, buyQuantity = 2, freeQuantity = 1)
        cart.applyOffer(product, offer)

        val unitPrice = 10.0
        val quantity = 5
        unitPrice * quantity
        val discountedPrice = unitPrice * 4
        val tax = taxCalculatorService.calculateTax(discountedPrice)
        val expectedTotalWithTax = discountedPrice + tax

        assertEquals(expectedTotalWithTax, cart.calculateTotalPrice(), 1.0)
    }


    @Test
    fun `percentage discount offer logic`() {
        cart.addItem(product, 3)
        val discount = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 20.0)
        cart.applyOffer(product, discount)

        // 20% discount on each item
        val expectedTotal = 10.0 * 3 * 0.8
        val tax = 30 * 0.1
        val total = expectedTotal + tax
        assertEquals(total, cart.calculateTotalPrice(), 0.01)
    }

    @Test
    fun `no offer logic`() {
        cart.addItem(product, 2)

        val expectedTotal = 10.0 * 2
        val tax = expectedTotal * 0.10
        val total = expectedTotal + tax
        assertEquals(total, cart.calculateTotalPrice(), 0.01)
    }
}


