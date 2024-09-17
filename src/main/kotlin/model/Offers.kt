package model

data class Offer(val type: OfferType, val buyQuantity: Int = 0, val freeQuantity: Int = 0, val percentageOff: Double = 0.0)

enum class OfferType {
    BUY_N_GET_M_FREE, PERCENTAGE_DISCOUNT
}
