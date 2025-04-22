import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class ShoppingCart extends Application {
    private final String CATALOG_FILE = "catalog.dat";
    private final String CUSTOMERS_FILE = "customers.dat";
    private final String SESSION_FILE = "session.dat";
    private final String ADMIN_PASSWORD = "admin123";

    private ArrayList<Product> catalog = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private Customer currentCustomer = null;
    private boolean adminLoggedIn = false;
    private int userCounter = 1;
    private Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Image icon = new Image("profile.jpg");
        primaryStage.getIcons().add(icon);
        loadCatalog();
        loadCustomers();
        loadSession();
        if (currentCustomer != null) {
            showShoppingUI();      
        } 
        else {
            showMainMenu();       
    }
    primaryStage.show();
    }
    

    private void showMainMenu() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle(
            "-fx-background-image: url('background.jpg'); " +
            "-fx-background-repeat: no-repeat; " +
            "-fx-background-size: cover;"
        );

        Label title = new Label("Shopping Cart App");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill:green ;");
        Button signUp = new Button("Sign Up");
        Button login = new Button("Login (Customer)");
        Button adminLogin = new Button("Login (Admin)");
        Button exit = new Button("Exit");

        for (Button btn : Arrays.asList(signUp, login, adminLogin, exit)) {
            btn.setStyle("-fx-background-color:White; -fx-text-fill: Purple; "
                       + "-fx-font-size: 16px; -fx-background-radius: 10; -fx-padding: 10 20 10 20;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:lightgray; -fx-text-fill: Purple; "
                       + "-fx-font-size: 16px; -fx-background-radius: 10; -fx-padding: 10 20 10 20;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: White; -fx-text-fill:Purple; "
                       + "-fx-font-size: 16px; -fx-background-radius: 10; -fx-padding: 10 20 10 20;"));
        }


        signUp.setOnAction(e -> {
            String name = prompt("Enter name:");
            String pass = prompt("Enter password:");
            if (name != null && pass != null) {
                String id = "U" + userCounter++;
                Customer c = new Customer(id, pass, name);
                customers.add(c);
                currentCustomer = c;
                saveCustomers();
                saveSession();
                alert("Signed up! Your ID is: " + id);
                showShoppingUI();
            }
        });


        login.setOnAction(e -> {
            String id = prompt("Enter User ID:");
            String pass = prompt("Enter Password:");
            for (Customer c : customers) {
                if (c.userId.equals(id) && c.password.equals(pass)) {
                    currentCustomer = c;
                    saveSession();
                    alert("Welcome " + c.name);
                    showShoppingUI();
                    return;
                }
            }
            alert("Invalid credentials.");
        });


        adminLogin.setOnAction(e -> {
            String pass = prompt("Enter Admin Password:");
            if (ADMIN_PASSWORD.equals(pass)) {
                adminLoggedIn = true;
                currentCustomer = null;
                showShoppingUI();
            } else {
                alert("Incorrect password.");
            }
        });


        exit.setOnAction(e -> {
            saveCatalog();
            saveCustomers();
            saveSession();
            Platform.exit();
        });

        layout.getChildren().addAll(title, signUp, login, adminLogin, exit);

        Scene scene = new Scene(layout, 1000, 600);
        window.setScene(scene);
        window.setTitle("Main Menu");
        window.show();
    }


    private void showShoppingUI() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #f7f7f7;");

        Label heading = new Label(adminLoggedIn ? "Catalog" : "Welcome, " + currentCustomer.name);
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        ScrollPane catalogPane = new ScrollPane();
        VBox catalogBox = new VBox(10);
        catalogBox.setPadding(new Insets(10));
        catalogBox.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        catalogPane.setContent(catalogBox);
        catalogPane.setFitToWidth(true);

        ScrollPane cartPane = new ScrollPane();
        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));
        cartBox.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        cartPane.setContent(cartBox);
        cartPane.setFitToWidth(true);

        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;");
        logout.setOnAction(e -> {
            currentCustomer = null;
            adminLoggedIn = false;
            saveSession();
            showMainMenu();
        });

        Button orderHistory = new Button("Order History");
        orderHistory.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; -fx-background-radius: 10;");
        orderHistory.setOnAction(e -> {
            alert("Order History:\n" + currentCustomer.getOrderHistory());
        });

        layout.getChildren().addAll(heading, catalogPane);
        if (!adminLoggedIn) {
            layout.getChildren().addAll(cartPane, orderHistory);
        }
        layout.getChildren().add(logout);

        updateCatalogUI(catalogBox, cartBox);
        window.setScene(new Scene(layout, 700, 650));
        window.setTitle(adminLoggedIn ? "Admin View" : "Customer View");
    }


    private void updateCatalogUI(VBox catalogBox, VBox cartBox) {
        catalogBox.getChildren().clear();
        if (cartBox != null) {
            cartBox.getChildren().clear();
        }
    
        
        for (int i = 0; i < catalog.size(); i++) {
            final Product product = catalog.get(i);
    
            HBox row = new HBox(10);
            row.setPadding(new Insets(10));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; "
                       + "-fx-border-radius: 5; -fx-background-radius: 5;");
    
            Label name = new Label(product.name + " - $" + product.price + " (Limit: " + product.limit + ")");
            name.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
            row.getChildren().add(name);
    

            if (!adminLoggedIn) {
                TextField qtyField = new TextField("1");
                qtyField.setPrefWidth(50);
                qtyField.setStyle("-fx-border-color: #cccccc; -fx-background-radius: 5; -fx-border-radius: 5;");
                Button addToCart = new Button("Add to Cart");
                Button buyNow    = new Button("Buy Now");
                for (Button btn : Arrays.asList(addToCart, buyNow)) {
                    btn.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; "
                               + "-fx-background-radius: 10; -fx-padding: 5 15 5 15;");
                }
    

                addToCart.setOnAction(e -> {
                    try {
                        int qty = Integer.parseInt(qtyField.getText());
                        if (qty <= 0) {
                            alert("Please enter a quantity greater than 0.");
                            return;
                        }
                        if (currentCustomer.getCartQty(product) + qty > product.limit) {
                            alert("Cannot add more than limit " + product.limit);
                            return;
                        }
                        currentCustomer.addToCart(product, qty);
                        saveCustomers();
                        updateCatalogUI(catalogBox, cartBox);
                    } catch (NumberFormatException ex) {
                        alert("Please enter a valid number.");
                    }
                });
    

                buyNow.setOnAction(e -> {
                    try {
                        int qty = Integer.parseInt(qtyField.getText());
                        if (qty <= 0 || qty > product.limit) {
                            alert("Enter quantity between 1 and " + product.limit);
                            return;
                        }
                        showPaymentOptions(
                            product, qty,
                            "Bought " + qty + " x " + product.name + " for $" + (product.price * qty)
                        );
                    } catch (NumberFormatException ex) {
                        alert("Please enter a valid number.");
                    }
                });
    
                row.getChildren().addAll(new Label("Qty:"), qtyField, addToCart, buyNow);
    
            } else {
                
                Button delete = new Button("Delete");
                delete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;");
                delete.setOnAction(e -> {
                    catalog.remove(product);
                    saveCatalog();
                    updateCatalogUI(catalogBox, cartBox);
                });
    
                Button edit = new Button("Edit");
                edit.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; -fx-background-radius: 10;");
                edit.setOnAction(e -> {
                    try {
                        float newPrice = Float.parseFloat(prompt("New price:"));
                        int   newLimit = Integer.parseInt(prompt("New limit:"));
    
                        
                        product.price = newPrice;
                        product.limit = newLimit;
    
                        
                        for (Customer c : customers) {
                            for (Product cp : c.cart) {
                                if (cp.id == product.id) {
                                    cp.price = newPrice;
                                    cp.limit = newLimit;
                                }
                            }
                        }

    
                        saveCatalog();
                        saveCustomers();
                        updateCatalogUI(catalogBox, cartBox);

                    } catch (Exception ex) {
                        alert("Invalid input.");
                    }
                });
    
                row.getChildren().addAll(edit, delete);
            }
    
            catalogBox.getChildren().add(row);
        }
    
        
        if (!adminLoggedIn && currentCustomer != null && cartBox != null) {
            Label cartTitle = new Label("--- Your Cart ---");
            cartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4e54c8;");
            cartBox.getChildren().add(cartTitle);
    
            for (int i = 0; i < currentCustomer.cart.size(); i++) {
                final Product product = currentCustomer.cart.get(i);
                int count = currentCustomer.cartCounts.get(i);
    
                HBox cartItem = new HBox(10);
                cartItem.setPadding(new Insets(5));
                cartItem.setAlignment(Pos.CENTER_LEFT);
                Label itemLabel = new Label(
                    product.name + " - Qty: " + count + " ($" + (product.price * count) + ")"
                );
                itemLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
    
                Button minus = new Button("-");
                Button plus  = new Button("+");
                Button remove= new Button("Remove");
                for (Button btn : Arrays.asList(minus, plus, remove)) {
                    btn.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; "
                               + "-fx-background-radius: 10;");
                }
    

                minus.setOnAction(e -> {
                    currentCustomer.removeFromCart(product, 1);
                    saveCustomers();
                    updateCatalogUI(catalogBox, cartBox);
                });

                plus.setOnAction(e -> {
                    if (currentCustomer.getCartQty(product) >= product.limit) {
                        alert("Cannot add more than limit " + product.limit);
                    } else {
                        currentCustomer.addToCart(product, 1);
                    }
                    saveCustomers();
                    updateCatalogUI(catalogBox, cartBox);
                });

                remove.setOnAction(e -> {
                    currentCustomer.removeFromCart(product, currentCustomer.getCartQty(product));
                    saveCustomers();
                    updateCatalogUI(catalogBox, cartBox);
                });
    
                cartItem.getChildren().addAll(itemLabel, minus, plus, remove);
                cartBox.getChildren().add(cartItem);
            }
    

            Button proceed = new Button("Proceed to Buy");
            proceed.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; "
                           + "-fx-background-radius: 10; -fx-padding: 10;");
            proceed.setOnAction(e ->
                showPaymentOptions(null, 0, "Order placed. Total: $" + currentCustomer.getCartTotal())
            );
            cartBox.getChildren().add(proceed);
        }
    
        
        if (adminLoggedIn) {
            Button addNew = new Button("Add New Product");
            addNew.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; -fx-background-radius: 10;");
            addNew.setOnAction(e -> {
                String name = prompt("Product name:");
                try {
                    float price = Float.parseFloat(prompt("Price:"));
                    int limit   = Integer.parseInt(prompt("Limit per user:"));
                    for (Product existing : catalog) {
                        if (existing.name.equalsIgnoreCase(name) && existing.price == price) {
                            alert("Product already exists with same name and price.");
                            return;
                        }
                    }

                    int id = catalog.size() + 100;
                    catalog.add(new Product(id, name, price, limit));
                    saveCatalog();
                    updateCatalogUI(catalogBox, cartBox);

                } catch (Exception ex) {
                    alert("Invalid input.");
                }
            });
    
            Button viewCustomers = new Button("View Customers");
            viewCustomers.setStyle("-fx-background-color: #4e54c8; -fx-text-fill: white; -fx-background-radius: 10;");
            viewCustomers.setOnAction(e -> {
                ChoiceDialog<Customer> dlg = new ChoiceDialog<>(customers.get(0), customers);
                dlg.setTitle("Customer Details");
                dlg.setHeaderText("Select a customer to view details:");
                dlg.setContentText("Customer:");
                Optional<Customer> choice = dlg.showAndWait();
                choice.ifPresent(c -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("User ID: ").append(c.userId).append("\n")
                      .append("Name: ").append(c.name).append("\n\n")
                      .append("--- Cart ---\n").append(c.getCartDetails()).append("\n\n")
                      .append("--- Order History ---\n").append(c.getOrderHistory());
                    alert(sb.toString());
                });
            });
    
            catalogBox.getChildren().addAll(addNew, viewCustomers);
        }
    }
    
    
    private void showPaymentOptions(Product p, int qty, String orderDetails) {
        if (p == null) {
            orderDetails = currentCustomer.getCartDetails();
        }

        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);
        paymentAlert.setTitle("Payment Options");
        paymentAlert.setHeaderText("Choose Payment Method");
        ButtonType cod = new ButtonType("Cash on Delivery");
        ButtonType upi = new ButtonType("UPI");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        paymentAlert.getButtonTypes().setAll(cod, upi, cancel);

        Optional<ButtonType> result = paymentAlert.showAndWait();
        if (result.isPresent() && !result.get().getText().equals("Cancel")) {
            String chosenMethod = result.get().getText();
            
            Alert continueAlert = new Alert(Alert.AlertType.CONFIRMATION);
            continueAlert.setTitle("Review Payment");
            continueAlert.setHeaderText("You have selected: " + chosenMethod);
            continueAlert.setContentText("Click 'Continue' to review your order details.");
            ButtonType continueButton = new ButtonType("Continue");
            ButtonType backButton = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);
            continueAlert.getButtonTypes().setAll(continueButton, backButton);
            
            Optional<ButtonType> continueResult = continueAlert.showAndWait();
            if (continueResult.isPresent() && continueResult.get() == continueButton) {
                 Alert orderAlert = new Alert(Alert.AlertType.CONFIRMATION);
                 orderAlert.setTitle("Confirm Order");
                 orderAlert.setHeaderText("Order Summary");
                 orderAlert.setContentText(orderDetails + "\n\nClick 'Place Order' to confirm your purchase.");
                 ButtonType placeOrder = new ButtonType("Place Order");
                 ButtonType cancelOrder = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                 orderAlert.getButtonTypes().setAll(placeOrder, cancelOrder);
                 
                 Optional<ButtonType> orderResult = orderAlert.showAndWait();
                 if (orderResult.isPresent() && orderResult.get() == placeOrder) {
                
                      if (!chosenMethod.equals("Cash on Delivery")) {
                          alert("Payment via " + chosenMethod + " successful!\n" + orderDetails);
                      }
                      if (currentCustomer != null) {
                        String fullDetails = orderDetails + " (Payment: " + chosenMethod + ")";
                        currentCustomer.addToOrderHistory(fullDetails);
                        
                          if (p == null) {
                              currentCustomer.clearCart();
                          }


                          saveCustomers();
                          showShoppingUI();
                      }
                 }
            }
        }
    }
    
    private String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(message);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }


    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    


    private void saveCatalog() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATALOG_FILE))) {
            oos.writeObject(catalog);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveCustomers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveSession() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(currentCustomer != null ? currentCustomer.userId : null);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadCatalog() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CATALOG_FILE))) {
            catalog = (ArrayList<Product>) ois.readObject();
        } catch (Exception ignored) { }
    }

   
    private void loadCustomers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CUSTOMERS_FILE))) {
            customers = (ArrayList<Customer>) ois.readObject();
            for (Customer c : customers) {
                int idNum = Integer.parseInt(c.userId.substring(1));
                if (idNum >= userCounter) {
                    userCounter = idNum + 1;
                }
            }
        }
         catch (Exception ignored) { }
    }

    private void loadSession() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SESSION_FILE))) {
            String id = (String) ois.readObject();
            currentCustomer = customers.stream().filter(c -> c.userId.equals(id)).findFirst().orElse(null);
        } catch (Exception ignored) { }
    }
}