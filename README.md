# Shopping Cart Implementation

## Assumptions:
- Products are added to the shopping cart with their name and price.
- The 2-for-1 offer applies to every pair of identical products.
- Discount offers are in a percentage 
- Only one offer is possible at a time
- No interactive UI or database integration is required.

## How to Run:
- To test the cart functionality, you can run the `ShoppingCartTest` class.
- The `calculateTotalPrice()` method calculates the final cost considering any offers and tax.
- The `buildReceipt()` method prints an itemized receipt, showing all the products and their quantities, with the total price.

### RUN AND SEE RECEIPT
- You can run the main file and see a print out of the itemised receipt in the console:
```terminal
===== Receipt =====
Cornflakes x3
  Base price: 9.0
  Price after discount: 6.0

Laptop x1
  Base price: 1000.0
  Price after discount: 900.0

====================
Total before tax: 1009.0
Total after discounts: 906.0
Tax: 90.6
Total price (with taxes): 996.6
```