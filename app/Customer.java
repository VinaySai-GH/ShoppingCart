import java.util.*;
import java.io.*;

class Customer implements Serializable {
    String userId;
    String password;
    String name;
    ArrayList<Product> cart = new ArrayList<>();
    ArrayList<Integer> cartCounts = new ArrayList<>();
    ArrayList<String> orderHistory = new ArrayList<>();

    Customer(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId);
    }
    
    void addToCart(Product p, int qty) {
        if (getTotalItemsInCart() + qty > 50) {
            System.out.println("You cannot add more than 50 items to your cart.");
            return;
        }
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).id == p.id) {
                cartCounts.set(i, cartCounts.get(i) + qty);
                return;
            }
        }
        cart.add(p);
        cartCounts.add(qty);
    }

    
    void removeFromCart(Product p, int qty) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).id == p.id) {
                int currentQty = cartCounts.get(i);
                if (qty >= currentQty) {
                    cart.remove(i);
                    cartCounts.remove(i);
                } else {
                    cartCounts.set(i, currentQty - qty);
                }
                return;
            }
        }
    }

    
    void clearCart() {
        cart.clear();
        cartCounts.clear();
    }

    
    int getCartQty(Product p) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).id == p.id) {
                return cartCounts.get(i);
            }
        }
        return 0;
    }

    
    int getTotalItemsInCart() {
        int total = 0;
        for (int count : cartCounts) {
            total += count;
        }
        return total;
    }

    
    float getCartTotal() {
        float total = 0;
        for (int i = 0; i < cart.size(); i++) {
            total += cart.get(i).price * cartCounts.get(i);
        }
        return total;
    }

    
    String getCartDetails() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cart.size(); i++) {
            sb.append(cart.get(i).name)
              .append(" x ")
              .append(cartCounts.get(i))
              .append(" - $")
              .append(cart.get(i).price * cartCounts.get(i))
              .append("\n");
        }
        sb.append("\nTotal: $").append(getCartTotal());
        return sb.toString();
    }


    void addToOrderHistory(String orderDetails) {
        orderHistory.add(orderDetails);
    }

    
    String getOrderHistory() {
        if (orderHistory.isEmpty()) {
            return "No orders placed yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (String order : orderHistory) {
            sb.append(order).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString(){
        return userId;
    }
    
    public boolean checkDiscountEligibility() {
        
        return getCartTotal() > 100;
    }

    
    public int calculateEstimatedDeliveryTime() {
        
        return 5;
    }

    public void logout() {
        
        System.out.println(this.name + " has logged out successfully.");
    }

    
    public float applyVoucher(String voucherCode) {
       
        return getCartTotal() * 0.9f;
    }

    
    public boolean hasSavedPaymentMethod() {
        
        return true;
    }
}
