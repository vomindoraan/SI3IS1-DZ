package rs.etf.si3is1.employee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import rs.etf.si3is1.entities.Employee;
import rs.etf.si3is1.entities.Inventory;
import rs.etf.si3is1.entities.InventoryPK;
import rs.etf.si3is1.entities.Product;
import rs.etf.si3is1.entities.Reservation;
import rs.etf.si3is1.entities.Revenue;
import rs.etf.si3is1.entities.Store;
import rs.etf.si3is1.messaging.Condition;
import rs.etf.si3is1.messaging.Identifiable;
import rs.etf.si3is1.utils.ConsoleUtils;

public class EmployeeApp implements Identifiable {
    public static void main(String... args) {
        EmployeeApp app = login();
        while (true) {
            app.menu();
        }
    }
    
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("EntitiesPU");
    private static final Scanner INPUT = new Scanner(System.in);
//    static {
//        Locale.setDefault(Locale.US);
//    }
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "conditionRequest")
    private static Topic conditionRequestTopic;
    @Resource(lookup = "conditionReply")
    private static Topic conditionReplyTopic;
    
    public static EmployeeApp login() {
        System.out.print(String.join("\n",
            "*** LOGIN ***",
            "Employee id: "
        ));
        
        EntityManager em = EMF.createEntityManager();
        
        Employee employee = null;
        do {
            int id = INPUT.nextInt();
            employee = em.find(Employee.class, id);
            if (employee == null) {
                System.err.println("Employee " + id + " doesn't exist, try again");
            }
        } while (employee == null);
        
        em.close();
        return new EmployeeApp(employee);
    }

    protected final Employee employee;

    public EmployeeApp(Employee employee) {
        this.employee = employee;
    }

    @Override
    public int getID() {
        return employee.getIdEmployee();
    }

    public void menu() {
        ConsoleUtils.clear();
        System.out.println(String.join("\n",
            "*** EMPLOYEE APP ***",
            "",
            "User:  " + employee,
            "Store: " + employee.getIdStore(),
            "",
            "1. Search",
            "2. Purchase",
            "3. Make reservation",
            "4. Fulfill reservation",
            "5. Condition check",
            "6. Show revenue",
            "0. Exit"
        ));
        ConsoleUtils.choice(new Runnable[] {
            ()->System.exit(0), this::search, this::purchase, this::makeReservation,
            this::fulfillReservation, this::conditionCheck, this::showRevenue
        });
        ConsoleUtils.pause();
    }

    public void search() {
        ConsoleUtils.clear();
        System.out.println(String.join("\n",
            "*** SEARCH ***",
            "1. By name",
            "2. By type"
        ));
        ConsoleUtils.choice(new Runnable[] {
            null, this::searchByName, this::searchByType
        });    
    }
    
    private void searchByName() {
        System.out.println("Product name (empty for all): ");
        INPUT.nextLine(); // Flush
        String name = INPUT.nextLine();
        
        EntityManager em = EMF.createEntityManager();
        
        TypedQuery<Product> q = em.createQuery(
            "SELECT p FROM Product p WHERE p.name LIKE '%" + name + "%'",
            Product.class);
        List<Product> products = q.getResultList();
        
        System.out.println("\nResults:");
        products.forEach(p -> System.out.println(p));
        
        em.close();
    }
    
    private void searchByType() {
        System.out.println("Product type (empty for all): ");
        INPUT.nextLine(); // Flush
        String type = INPUT.nextLine();

        EntityManager em = EMF.createEntityManager();

        TypedQuery<Product> q = em.createQuery(
            "SELECT p FROM Product p WHERE p.type LIKE '%" + type + "%'",
            Product.class);
        List<Product> products = q.getResultList();

        System.out.println("\nResults:");
        products.forEach(p -> System.out.println(p));

        em.close();
    }
    
    public void purchase() {
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** PURCHASE ***",
            "Product id: "
        ));
        int id = INPUT.nextInt();
        
        EntityManager em = EMF.createEntityManager();
        
        Product p = em.find(Product.class, id);
        if (p == null) {
            System.err.println("Product " + id + " doesn't exist");
            em.close();
            return;
        }
        
        performPurchase(em, p);
        
        em.close();
    }
    
    private void performPurchase(EntityManager em, Product p) {
        int idStore = employee.getIdStore().getIdStore(); // First returns Store, second int
        Inventory inv = em.find(Inventory.class, new InventoryPK(idStore, p.getIdProduct()));
        Revenue rev = getRevenue(em);

        float maxAmount = inv.getAmount();
        if (maxAmount == 0) {
            System.out.println("Out of stock, make a reservation instead ([y]/n)?");
            INPUT.nextLine(); // Flush
            if (!INPUT.nextLine().toLowerCase().equals("n")) {
                makeReservation();
            }
            return;
        }

        System.out.printf("How much (max %.2f)? ", maxAmount);
        float amount = INPUT.nextFloat();
        if (amount > maxAmount) {
            System.err.println("Clamping to " + maxAmount);
            amount = maxAmount;
        }
        BigDecimal total = p.getPrice().multiply(BigDecimal.valueOf(amount));
        
        System.out.printf(String.join("\n",
            "You are about to purchase %.2f of: %s",
            "Total price: %.2f",
            "Are you sure ([y]/n)? "
        ), amount, p, total);
        INPUT.nextLine(); // Flush
        if (INPUT.nextLine().toLowerCase().equals("n")) {
            em.close();
            return;
        }
        
        em.getTransaction().begin();
        
        inv.setAmount(inv.getAmount() - amount);
        rev.setAmount(rev.getAmount() + amount);
        rev.setProfit(rev.getProfit().add(total));
        System.out.println("Purchase completed");
        
        em.getTransaction().commit();
    }
    
    private Store chooseStore(EntityManager em, Product product) {
        TypedQuery<Inventory> q = em.createQuery(
            "SELECT i FROM Inventory i WHERE i.inventoryPK.idProduct = :idProduct",
            Inventory.class);
        q.setParameter("idProduct", product.getIdProduct());
        
        List<Inventory> results = q.getResultList();
        if (results.isEmpty()) {
            System.err.println("No stores have the product in stock");
            return null;
        }
        Map<Integer, Store> storeMap = new HashMap<>();
        
        System.out.println();
        results.forEach(inv -> {
            int idStore = inv.getInventoryPK().getIdStore();
            Store store = em.find(Store.class, idStore);
            storeMap.put(idStore, store);
            // TODO Ask store manager for condition
            System.out.printf("%s – amount: %.2f\n", store, inv.getAmount());
        });
        
        System.out.print("\n> ");
        int id = INPUT.nextInt();
        if (!storeMap.containsKey(id)) {
            System.err.println("Store " + id + " doesn't exist");
            return null;
        }
        return storeMap.get(id);
    }
    
    public void makeReservation() {
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** MAKE RESERVATION ***",
            "Product id: "
        ));
        int id = INPUT.nextInt();

        EntityManager em = EMF.createEntityManager();

        Product p = em.find(Product.class, id);
        if (p == null) {
            System.err.println("Product " + id + " doesn't exist");
            em.close();
            return;
        }
        
        Store store = chooseStore(em, p);
        if (store == null) {
            em.close();
            return;
        }

        System.out.print("Customer name? ");
        INPUT.nextLine(); // Flush
        String customerName = INPUT.nextLine();
        System.out.print("Customer phone number? ");
        String customerPhoneNo = INPUT.nextLine();

        System.out.printf(String.join("\n",
            "You are about to reserve %s for %s",
            "Are you sure ([y]/n)? "
        ), p, customerName);
        if (INPUT.nextLine().toLowerCase().equals("n")) {
            em.close();
            return;
        }

        em.getTransaction().begin();

        Reservation r = new Reservation();
        r.setIdStore(store);
        r.setIdProduct(p);
        r.setCustomerName(customerName);
        if (!customerPhoneNo.isEmpty()) {
            r.setCustomerPhoneNo(customerPhoneNo);
        }
        em.persist(r);
        
        em.getTransaction().commit();
        em.close();
    }
    
    public void fulfillReservation() {
        // Ask for name, list reservations for that name, ask for reservation getID,
        // check if there's enough in stock, if not check in other stores
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** FULFILL RESERVATION ***",
            "Customer name: "
        ));
        INPUT.nextLine(); // Flush
        String customerName = INPUT.nextLine();
        
        EntityManager em = EMF.createEntityManager();
        
        TypedQuery<Reservation> q = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.customerName = :customerName AND r.idStore = :idStore",
            Reservation.class);
        q.setParameter("customerName", customerName).setParameter("idStore", employee.getIdStore());
        
        List<Reservation> reservations = q.getResultList();
        if (reservations.isEmpty()) {
            System.err.println("Customer has no reservations in this store");
            em.close();
            return;
        }
        Map<Integer, Reservation> reservationMap = reservations.stream().collect(
                Collectors.toMap(r -> r.getIdReservation(), r -> r));
        
        System.out.println("\nReservations:");
        reservations.forEach(r -> System.out.println(r));
        System.out.print("\n> ");
        int id = INPUT.nextInt();
        
        if (reservationMap.containsKey(id)) {
            Reservation r = reservationMap.get(id);
            performPurchase(em, r.getIdProduct());
            
            em.getTransaction().begin();
            em.remove(r);
            em.getTransaction().commit();
        } else {
            System.err.println("Reservation " + id + " doesn't exist");
        }
        
        em.close();
    }

    public void conditionCheck() {
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** CONDITION CHECK ***",
            "Product id: "
        ));
        int idProduct = INPUT.nextInt();
        int idStore = employee.getIdStore().getIdStore();

        EntityManager em = EMF.createEntityManager();
        Product p = em.find(Product.class, idProduct);
        em.close();
        if (p == null) {
            System.err.println("Product " + idProduct + " doesn't exist");
            return;
        }
        
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(
            conditionReplyTopic, "idStore = " + idStore + " AND idProduct = " + idProduct);
        
        try {
            Message msg = context.createMessage();
            msg.setIntProperty("idStore", idStore);
            msg.setIntProperty("idProduct", idProduct);
            producer.send(conditionRequestTopic, msg);
        } catch (JMSException e) {
            System.err.println("Error while sending condition request: " + e.getMessage());
            return;
        }
        
        Condition cond = consumer.receiveBody(Condition.class);
        System.out.println("Condition: " + cond.name().toLowerCase());
        
        context.close();
    }
    
    private Revenue getRevenue(EntityManager em) {
        Store store = employee.getIdStore();
        Date date = new Date();

        TypedQuery<Revenue> q = em.createQuery(
            "SELECT t FROM Revenue t WHERE t.idStore = :store AND t.date = :date",
            Revenue.class);
        q.setParameter("store", store).setParameter("date", date);

        List<Revenue> results = q.getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }

        em.getTransaction().begin();

        Revenue rev = new Revenue();
        rev.setIdStore(store);
        rev.setDate(date);
        rev.setAmount(0);
        rev.setProfit(BigDecimal.ZERO);
        em.persist(rev);

        em.getTransaction().commit();
        return rev;
    }
    
    public void showRevenue() {
        EntityManager em = EMF.createEntityManager();
        Revenue rev = getRevenue(em);
        em.close();
        
        ConsoleUtils.clear();
        System.out.println(String.join("\n",
            "*** SHOW REVENUE ***",
            "Store:  " + rev.getIdStore(),
            "Date:   " + rev.getDate(),
            "Amount: " + rev.getAmount(),
            "Profit: " + rev.getProfit()
        ));
    }
}
