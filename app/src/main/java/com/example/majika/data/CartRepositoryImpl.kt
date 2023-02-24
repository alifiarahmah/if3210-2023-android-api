package com.example.majika.data

import com.example.majika.data.dao.CartDao
import com.example.majika.data.entity.Cart

class CartRepositoryImpl(private val dao: CartDao) : CartRepository {

    override fun getCart(): List<Cart> {
        return dao.getAll()
    }

    override fun getCartByAttr(name: String, type: MenuType, price: Int): Cart {
        return dao.getByAttr(name, type.toString(), price)
    }

    override fun isInCart(name: String, type: MenuType, price: Int): Boolean {
        val cart = dao.getByAttr(name, type.toString(), price)
        return cart != null
    }

    override fun addToCart(cart: Cart) {
        dao.insert(cart)
    }

    override fun removeFromCart(cart: Cart) {
        dao.delete(cart)
    }

    override fun increaseQuantity(cart: Cart) {
        val quantity = dao.getQuantityByName(cart.name)
        cart.quantity = quantity + 1
        dao.update(cart)
    }

    override fun decreaseQuantity(cart: Cart) {
        // if quantity is 1, delete the item
        if (cart.quantity == 1) {
            removeFromCart(cart)
            return
        }
        val quantity = dao.getQuantityByName(cart.name)
        cart.quantity = quantity - 1
        dao.update(cart)
    }

    override fun clearCart(cartList: List<Cart>) {
        dao.deleteAll(cartList)
    }
}