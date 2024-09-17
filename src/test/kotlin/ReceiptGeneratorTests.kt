import cart.ShoppingCart
import model.Category
import model.Offer
import model.OfferType
import model.Product
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import service.ReceiptGeneratorService
import service.TaxCalculatorService
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class ReceiptGeneratorTest {
    private lateinit var cart: ShoppingCart
    private lateinit var cornflakes: Product
    private lateinit var laptop: Product
    private lateinit var taxCalculatorService: TaxCalculatorService

    @BeforeEach
    fun setup() {
        cart = ShoppingCart()
        taxCalculatorService = TaxCalculatorService()

        cornflakes = Product("Cornflakes", "SKU123", 3.0, Category.GROCERY, "Brand A")
        laptop = Product("Laptop", "SKU456", 1000.0, Category.ELECTRONICS, "Brand B")
    }

    @Test
    fun `generate receipt with no discounts or offers`() {
        cart.addItem(cornflakes, 2)
        cart.addItem(laptop, 1)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Cornflakes x2"))
        assertTrue(receipt.contains("  Base price: 6.0"))
        assertTrue(receipt.contains("  Price after discount: 6.0"))

        assertTrue(receipt.contains("Laptop x1"))
        assertTrue(receipt.contains("  Base price: 1000.0"))
        assertTrue(receipt.contains("  Price after discount: 1000.0"))

        val expectedBaseTotal = 6.0 + 1000.0
        val expectedDiscountTotal = 6.0 + 1000.0  // No discount applied
        val expectedTax = BigDecimal(expectedDiscountTotal * 0.10).setScale(1, RoundingMode.HALF_UP).toDouble()
        val expectedTotalWithTax = expectedDiscountTotal + expectedTax

        assertTrue(receipt.contains("Total before tax: $expectedBaseTotal"))
        assertTrue(receipt.contains("Total after discounts: $expectedDiscountTotal"))
        assertTrue(receipt.contains("Tax: $expectedTax"))
        assertTrue(receipt.contains("Total price (with taxes): $expectedTotalWithTax"))
    }

    @Test
    fun `generate receipt with percentage discount`() {
        cart.addItem(laptop, 1)

        val discountOffer = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 10.0)
        cart.applyOffer(laptop, discountOffer)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Laptop x1"))
        assertTrue(receipt.contains("  Base price: 1000.0"))
        assertTrue(receipt.contains("  Price after discount: 900.0"))

        val expectedBaseTotal = 1000.0
        val expectedDiscountTotal = 900.0  // 10% discount applied
        val expectedTax = expectedDiscountTotal * 0.10
        val expectedTotalWithTax = expectedDiscountTotal + expectedTax

        assertTrue(receipt.contains("Total before tax: $expectedBaseTotal"))
        assertTrue(receipt.contains("Total after discounts: $expectedDiscountTotal"))
        assertTrue(receipt.contains("Tax: $expectedTax"))
        assertTrue(receipt.contains("Total price (with taxes): $expectedTotalWithTax"))
    }

    @Test
    fun `generate receipt with buy N get M free offer`() {
        cart.addItem(cornflakes, 6)

        val buyTwoGetOneFreeOffer = Offer(OfferType.BUY_N_GET_M_FREE, buyQuantity = 2, freeQuantity = 1)
        cart.applyOffer(cornflakes, buyTwoGetOneFreeOffer)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Cornflakes x6"))
        assertTrue(receipt.contains("  Base price: 18.0"))
        assertTrue(receipt.contains("  Price after discount: 12.0"))

        val expectedBaseTotal = 18.0
        val expectedDiscountTotal = 12.0
        val expectedTax = BigDecimal(expectedDiscountTotal * 0.10).setScale(1, RoundingMode.HALF_UP).toDouble()
        val expectedTotalWithTax = expectedDiscountTotal + expectedTax

        assertTrue(receipt.contains("Total before tax: $expectedBaseTotal"))
        assertTrue(receipt.contains("Total after discounts: $expectedDiscountTotal"))
        assertTrue(receipt.contains("Tax: $expectedTax"))
        assertTrue(receipt.contains("Total price (with taxes): $expectedTotalWithTax"))
    }

    @Test
    fun `generate receipt with mixed offers`() {
        cart.addItem(cornflakes, 4)
        cart.addItem(laptop, 1)

        val buyTwoGetOneFreeOffer = Offer(OfferType.BUY_N_GET_M_FREE, buyQuantity = 2, freeQuantity = 1)
        cart.applyOffer(cornflakes, buyTwoGetOneFreeOffer)

        val laptopDiscount = Offer(OfferType.PERCENTAGE_DISCOUNT, percentageOff = 10.0)
        cart.applyOffer(laptop, laptopDiscount)

        val receipt = cart.buildReceipt()

        assertTrue(receipt.contains("Cornflakes x4"))
        assertTrue(receipt.contains("  Base price: 12.0"))
        assertTrue(receipt.contains("  Price after discount: 9.0"))

        assertTrue(receipt.contains("Laptop x1"))
        assertTrue(receipt.contains("  Base price: 1000.0"))
        assertTrue(receipt.contains("  Price after discount: 900.0"))  // 10% discount applied

        val expectedBaseTotal = 12.0 + 1000.0
        val expectedDiscountTotal = 9.0 + 900.0
        val expectedTax = expectedDiscountTotal * 0.10
        val expectedTotalWithTax = expectedDiscountTotal + expectedTax

        assertTrue(receipt.contains("Total before tax: $expectedBaseTotal"))
        assertTrue(receipt.contains("Total after discounts: $expectedDiscountTotal"))
        assertTrue(receipt.contains("Tax: $expectedTax"))
        assertTrue(receipt.contains("Total price (with taxes): $expectedTotalWithTax"))
    }

    @Test
    fun `generate receipt with no items`() {
        val receipt = cart.buildReceipt()
        assertTrue(receipt.contains("===== Receipt ====="))
        assertTrue(receipt.contains("Total before tax: 0.0"))
        assertTrue(receipt.contains("Total after discounts: 0.0"))
        assertTrue(receipt.contains("Tax: 0.0"))
        assertTrue(receipt.contains("Total price (with taxes): 0.0"))
    }
}
