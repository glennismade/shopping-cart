package service

import model.Offer
import model.OfferType

class OfferService {
    fun calculatePriceAfterOffer(
        quantity: Int,
        unitPrice: Double,
        offer: Offer?
    ): Double {
        return when (offer?.type) {
            OfferType.BUY_N_GET_M_FREE -> {
                val chargeableQuantity = quantity - (quantity / (offer.buyQuantity + offer.freeQuantity)) * offer.freeQuantity
                chargeableQuantity * unitPrice
            }
            OfferType.PERCENTAGE_DISCOUNT -> {
                val discountAmount = unitPrice * (offer.percentageOff / 100)
                val discountedUnitPrice = unitPrice - discountAmount
                discountedUnitPrice * quantity
            }
            else -> unitPrice * quantity
        }
    }

    fun buildOfferPrice(
        quantity: Int,
        unitPrice: Double,
        offer: Offer?
    ): Double {
        return calculatePriceAfterOffer(quantity, unitPrice, offer)
    }
}
