package synchronizedDemo.synchronized01.practice;

public class ProductInventory {

    private  int stock;

    private static int totalOrders=0;

    public ProductInventory(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public static int getTotalOrders() {
        return totalOrders;
    }

    public static void setTotalOrders(int totalOrders) {
        ProductInventory.totalOrders = totalOrders;
    }

    public    synchronized void  purchase(){

        if(stock>0){
            stock--;
            System.out.println("Thread Running--->"+Thread.currentThread().getName());

            incrementTotalOrders();
            System.out.println(
                    Thread.currentThread().getName()
                            + " purchased product. Remaining stock: " + stock
            );
        }else{
            System.out.println("Product out of stock");
        }


    }

    private  static void incrementTotalOrders(){
        totalOrders++;
    }


    public static void main(String[] args) throws InterruptedException {



        ProductInventory inventory1=new ProductInventory(1);

        Runnable task =()->{
            System.out.println("Task nis running--->"+Thread.currentThread().getName());
            inventory1.purchase();
        };

        Thread thread1=new Thread(task,"Thread-1");
        Thread thread2=new Thread(task,"Thread-2");


        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("final stock--->"+inventory1.stock);
        System.out.println("total orders placed successfully---->"+totalOrders);


    }
}
