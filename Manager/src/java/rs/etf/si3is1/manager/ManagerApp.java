package rs.etf.si3is1.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import rs.etf.si3is1.entities.Manager;
import rs.etf.si3is1.entities.Product;
import rs.etf.si3is1.entities.Store;
import rs.etf.si3is1.messaging.Condition;
import rs.etf.si3is1.messaging.Identifiable;
import rs.etf.si3is1.utils.ConsoleUtils;

public class ManagerApp implements Identifiable, Runnable {
    public static void main(String... args) {
        ManagerApp app = login();
        app.run();
    }
    
    public static final long DEFAULT_TIMEOUT = 250; // ms

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("EntitiesPU");
    private static final Scanner INPUT = new Scanner(System.in);
//    static {
//        Locale.setDefault(Locale.US);
//    }
    private static final Random RANDOM = new Random();

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(lookup = "conditionRequest")
    private static Topic conditionRequestTopic;
    @Resource(lookup = "conditionReply")
    private static Topic conditionReplyTopic;

    public static ManagerApp login() {
        System.out.print(String.join("\n",
            "*** LOGIN ***",
            "Manager id: "
        ));

        EntityManager em = EMF.createEntityManager();

        while (true) {
            int id = INPUT.nextInt();
            Manager manager = em.find(Manager.class, id);
            if (manager == null) {
                System.err.println("Manager " + id + " doesn't exist, try again");
                continue;
            }
            
            List<Store> stores = manager.getStoreList();
            if (stores.isEmpty()) {
                System.err.println("Manager " + id + " doesn't manage a store, try again");
                continue;
            }
            
            em.close();
            return new ManagerApp(manager, stores.get(0));
        }
    }

    protected final Manager manager;
    protected final Store store;
    protected final long timeout;
    protected final Map<Product, Condition> conditionMap;
    
    public ManagerApp(Manager manager, Store store) {
        this(manager, store, DEFAULT_TIMEOUT);
    }

    public ManagerApp(Manager manager, Store store, long timeout) {
        this.manager = manager;
        this.store = store;
        this.timeout = timeout;
        this.conditionMap = new HashMap<>();
    }
    
    @Override
    public void run() {
        ConsoleUtils.clear();
        System.out.println(String.join("\n",
            "*** EMPLOYEE APP ***",
            "",
            "User:  " + manager,
            "Store: " + store,
            ""
        ));
        JMSContext context = connectionFactory.createContext();
        context.setClientID(getIDString());
        JMSProducer producer = context.createProducer();
        JMSConsumer consumerCond = context.createDurableConsumer(
            conditionRequestTopic, getIDString(), "idStore = " + store.getIdStore(), false);
        while (true) {
            // Each times out after 250 ms
            handleConditionCheck(context, producer, consumerCond);
//            handleReservation(context);
//            handleNewProducts(context);
//            handleNewPrice(context);
        }
//        context.close();
    }
    
    @Override
    public int getID() {
        return manager.getIdManager();
    }
    
    public Condition checkCondition(Product product) {
        Condition cond = (RANDOM.nextDouble() < 0.7) ? Condition.UNOPENED : Condition.OPENED;
        conditionMap.putIfAbsent(product, cond);
        return conditionMap.get(product);
    }
    
    private void handleConditionCheck(JMSContext context, JMSProducer producer, JMSConsumer consumer) {
        try {
            Message msgRecv = consumer.receive(timeout);
            if (msgRecv == null) {
                return;
            }
            int idStore = store.getIdStore();
            int idProduct = msgRecv.getIntProperty("idProduct");

            EntityManager em = EMF.createEntityManager();
            Product p = em.find(Product.class, idProduct);
            em.close();

            Condition cond = checkCondition(p);
            System.out.printf("Condition check: (idStore=%d, idProduct=%d) => %s\n",
                    idStore, idProduct, cond.name());

            ObjectMessage msgSend = context.createObjectMessage(cond);
            msgSend.setIntProperty("idStore", idStore);
            msgSend.setIntProperty("idProduct", idProduct);
            producer.send(conditionReplyTopic, msgSend);
        } catch (JMSException e) {
            System.err.println("Error while handling condition check: " + e.getMessage());
        }
    }
}
