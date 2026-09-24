package thread.creation.extend.ecommerce;

public class EmailNotification extends Thread{

    private Order order;

    public EmailNotification(Order order) {
        this.order = order;
    }


    @Override
    public void run(){

        System.out.println("sending email for order : "+order.getOrderId());

        //here lets assume we have the implementation for email notification
        try{
            Thread.sleep(2000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("email sent to customer :" +order.getCustomerName());
    }
}
