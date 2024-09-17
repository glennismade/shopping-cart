import cart.ShoppingCart
import model.Category
import model.Product

object TestFactory {
    fun createShoppingCart(): ShoppingCart {
        return ShoppingCart()
    }

    fun createProduct(name: String, sku: String, price: Double, category: Category, brand: String): Product {
        return Product(name, sku, price, category, brand)
    }
}
