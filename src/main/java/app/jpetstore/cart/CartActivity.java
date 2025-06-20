/*
 * Copyright (c) 2018-present The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.jpetstore.cart;

import app.jpetstore.cart.domain.Cart;
import app.jpetstore.cart.domain.CartItem;
import app.jpetstore.cart.service.CartService;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Action;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Redirect;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.core.component.bean.annotation.Required;

import java.util.Iterator;

/**
 * The Class CartActivity.
 *
 * @author Juho Jeong
 */
@Component
public class CartActivity {

    private final CartService cartService;

    @Autowired
    public CartActivity(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds the item to the cart.
     */
    @Request("/cart/addItemToCart")
    @Redirect("/cart/viewCart")
    @Action("cart")
    public Cart addItemToCart(@Required String itemId) {
        cartService.addItemToCart(itemId);
        return cartService.getCart();
    }

    /**
     * Removes the item from the cart.
     */
    @RequestToPost("/cart/removeItemFromCart")
    @Dispatch(name = "cart/Cart", dispatcher = "jspView")
    @Action("cart")
    public Cart removeItemFromCart(@Required String cartItem) {
        cartService.removeItemFromCart(cartItem);
        return cartService.getCart();
    }

    /**
     * Removes all items from cart.
     */
    @RequestToPost("/cart/removeAllItemsFromCart")
    @Dispatch(name = "cart/Cart", dispatcher = "jspView")
    @Action("cart")
    public Cart removeAllItemsFromCart() {
        cartService.removeAllItemsFormCart();
        return cartService.getCart();
    }

    /**
     * Update cart quantities.
     */
    @RequestToPost("/cart/updateCartQuantities")
    @Dispatch(name = "cart/Cart", dispatcher = "jspView")
    @Action("cart")
    public Cart updateCartQuantities(Translet translet) {
        Iterator<CartItem> cartItems = cartService.getAllCartItems();
        while (cartItems.hasNext()) {
            CartItem cartItem = cartItems.next();
            String itemId = cartItem.getItem().getItemId();
            try {
                int quantity = Integer.parseInt(translet.getParameter(itemId));
                cartService.setQuantityByItemId(itemId, quantity);
                if (quantity < 1) {
                    cartItems.remove();
                }
            } catch (Exception e) {
                // ignore parse exceptions on purpose
            }
        }
        return cartService.getCart();
    }

    @Request("/cart/viewCart")
    @Dispatch("cart/Cart")
    @Action("cart")
    public Cart viewCart() {
        return cartService.getCart();
    }

    @Request("/cart/checkOut")
    @Dispatch("cart/Checkout")
    public Cart checkOut() {
        return viewCart();
    }

}
