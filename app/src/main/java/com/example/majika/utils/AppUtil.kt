package com.example.majika.utils

import java.text.NumberFormat
import java.util.*

class AppUtil {
    companion object {
        fun toRupiah(number: Int): String{
            val localeID =  Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            return numberFormat.format(number).toString()
        }

        fun formatNumber(number: Int): String {
            return NumberFormat.getNumberInstance().format(number)
        }
    }
}