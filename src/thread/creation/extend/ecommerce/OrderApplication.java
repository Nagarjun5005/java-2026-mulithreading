package thread.creation.extend.ecommerce;

public class OrderApplication {

    public static void main(String[] args) {
        System.out.println("Processing order...");

        Order order = new Order(101, "Nagarjun");

        System.out.println("Order saved successfully.");


        EmailNotification emailThread =
                new EmailNotification(order);

        emailThread.start();

        System.out.println("order processing completed!");
    }
}
