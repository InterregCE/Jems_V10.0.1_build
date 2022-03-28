package io.cloudflight.jems.server.currency.entity

data class EuroExchangeRate (
    val country: String,
    val currency: String,
    val isoA3Code: String,
    val isoA2Code: String,
    val value: String,
    val comment: String?
)
