# ProductInventory — Output Explanation

## 1. Initial State

We create two inventory objects:

```java
ProductInventory inventory1 = new ProductInventory(5);
ProductInventory inventory2 = new ProductInventory(5);
```

Initial values:

```text
inventory1.stock = 5
inventory2.stock = 5
totalOrders = 0
```

- `stock` is an instance variable. Each inventory object has its own stock.
- `totalOrders` is a static variable. All inventory objects share the same counter.

## 2. Thread and Task Mapping

| Thread | Task | Inventory | Purchase attempts |
|---|---|---|---:|
| Thread-1 | task1 | inventory1 | 5 |
| Thread-2 | task2 | inventory1 | 5 |
| Thread-3 | task3 | inventory2 | 5 |

There are 15 purchase attempts, but only 10 products are available in total.

## 3. Why Thread-3 Can Execute Alongside Thread-1

The `purchase()` method is an instance synchronized method:

```java
public synchronized void purchase() {
    // ...
}
```

An instance synchronized method locks the monitor of the object on which it is called.

- Thread-1 and Thread-2 call `inventory1.purchase()`. They compete for `inventory1`'s monitor.
- Thread-3 calls `inventory2.purchase()`. It uses `inventory2`'s monitor.

Because these are different monitors, Thread-3 can execute independently of the other threads' instance synchronization.

Calling `start()` in a particular order does not guarantee the order in which threads execute. That is why Thread-3 can print before Thread-1.

## 4. How Stock Decreases

Each successful purchase decrements the stock by one:

```java
stock--;
```

For inventory1:

```text
Initial stock: 5
Purchase 1:    4
Purchase 2:    3
Purchase 3:    2
Purchase 4:    1
Purchase 5:    0
```

For inventory2:

```text
Initial stock: 5
Purchase 1:    4
Purchase 2:    3
Purchase 3:    2
Purchase 4:    1
Purchase 5:    0
```

Thread-1 and Thread-2 collectively attempt 10 purchases against inventory1, which contains only five products. Thread-3 attempts five purchases against inventory2, which also contains five products.

Only five purchases can succeed for each inventory.

## 5. Why Thread-2 Reports Out of Stock

Your output shows:

```text
Thread-2 -> Product out of stock
Thread-2 -> Product out of stock
Thread-2 -> Product out of stock
Thread-2 -> Product out of stock
Thread-2 -> Product out of stock
```

In this particular execution, Thread-1 completed all five successful purchases from inventory1 before Thread-2 could make a successful purchase.

Thread-2 still makes its five attempts, but inventory1's stock is already zero.

This result is valid. Java does not guarantee fair access to a synchronized method. In another execution, Thread-2 could make some of the successful purchases instead.

## 6. How Static Synchronization Protects totalOrders

The successful purchase calls:

```java
incrementTotalOrders();
```

The static synchronized method is:

```java
private static synchronized void incrementTotalOrders() {
    totalOrders++;
    System.out.println("Total orders: " + totalOrders);
}
```

Because this method is static and synchronized, it uses the monitor associated with:

```java
ProductInventory.class
```

All threads updating `totalOrders` through this method compete for the same class monitor.

Only one thread at a time can execute the method, preventing lost updates to the counter.

The counter progresses from:

```text
0 → 1 → 2 → 3 → ... → 10
```

Each successful purchase increments the counter exactly once.

The order of the counter messages can vary because the threads execute concurrently.

## 7. Final Calculation

| Thread | Successful purchases |
|---|---:|
| Thread-1 | 5 |
| Thread-2 | 0 |
| Thread-3 | 5 |
| **Total** | **10** |

The expected final output is:

```text
Inventory 1 remaining stock: 0
Inventory 2 remaining stock: 0
Total orders: 10
```

## 8. Why join() Is Used

```java
thread1.join();
thread2.join();
thread3.join();
```

The main thread waits until all three worker threads finish before printing the final results.

`join()` does not protect the shared variables from race conditions. Synchronization is provided by the synchronized methods.

## 9. Key Takeaways

1. **Instance synchronization:** Protects access through a particular object's monitor.
2. **Static synchronization:** Uses the class object's monitor, shared across all instances.
3. **Thread scheduling:** The order of execution and log messages can vary.
4. **Stock validation:** Each inventory can sell only as many products as it has available.
5. **Shared counter:** Every successful purchase increments the same `totalOrders` variable.
6. **join():** Ensures the main thread waits for all worker threads to finish before printing final results.

**Remember:** Different inventory objects have different instance monitors, but all calls to the static synchronized method use the same class monitor.
