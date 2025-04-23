import java.io.*;
import java.util.*;
class Product implements Serializable {
    int id;
    String name;
    float price;
    int limit;

    Product(int id, String name, float price, int limit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.limit = limit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product prod = (Product) o;
        return id == prod.id;
    }




    public String toString() {
        return id + ": " + name + " ($" + price + ", Limit: " + limit + ")";
    }
    
    public String getDescription() {
        return name + " costs $" + price;
    }

    
    public boolean isInStock(int desiredQuantity) {
        return desiredQuantity <= limit;
    }

    
    public void applyDiscount(float percent) {
        if (percent > 0 && percent < 100) {
            price = price * (1 - percent / 100);
        }
    }

    
    public void restock(int additionalUnits) {
        if (additionalUnits > 0) {
            limit += additionalUnits;
        }
    }
    public double getPriceWithTax(float taxRate) {
        return price * (1 + taxRate / 100);
    }
}

