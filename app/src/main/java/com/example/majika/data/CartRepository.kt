package com.example.majika.data

import com.example.majika.data.entity.Cart

interface CartRepository {
    fun getCart(): List<Cart>
    fun getCartByAttr(name: String, type: MenuType, price: Int): Cart?
    fun isInCart(name: String, type: MenuType, price: Int): Boolean
    fun addToCart(cart: Cart)
    fun removeFromCart(cart: Cart)
    fun increaseQuantity(cart: Cart)
    fun decreaseQuantity(cart: Cart)
    fun clearCart(cartList: List<Cart>)
}