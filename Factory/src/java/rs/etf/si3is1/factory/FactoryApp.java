package rs.etf.si3is1.factory;

import java.math.BigDecimal;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import rs.etf.si3is1.entities.FProduct;
import rs.etf.si3is1.messaging.Identifiable;
import rs.etf.si3is1.utils.ConsoleUtils;

public class FactoryApp implements Identifiable {
    public static void main(String... args) {
        FactoryApp app = FactoryApp.getInstance();
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
    @Resource(lookup = "newProducts")
    private static Queue newProductsQueue;
    @Resource(lookup = "newPrice")
    private static Queue newPriceQueue;

    private static FactoryApp instance = null;
    
    public static FactoryApp getInstance() {
        if (instance == null) {
            instance = new FactoryApp();
        }
        return instance;
    }
    
    protected FactoryApp() {}

    @Override
    public int getID() {
        return 0;
    }
    
    public void menu() {
        ConsoleUtils.clear();
        System.out.println(String.join("\n",
            "*** FACTORY APP ***",
            "1. Produce",
            "2. Change MSRP",
            // "3. Update spec",
            "0. Exit"
        ));
        ConsoleUtils.choice(new Runnable[] {
            ()->System.exit(0), this::produce, this::changeMSRP//, this::updateSpec
        });
        ConsoleUtils.pause();
    }
    
    public void produce() {
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** PRODUCE ***",
            "Product id: "
        ));
        int id = INPUT.nextInt();

        EntityManager em = EMF.createEntityManager();
        FProduct fp = em.find(FProduct.class, id);
        em.close();
        if (fp == null) {
            System.err.println("Product " + id + " doesn't exist");
            return;
        }

        System.out.print("How many? ");
        float amount = INPUT.nextFloat();
        if (amount <= 0) {
            System.err.println("Amount cannot be 0 or less");
            return;
        }
        
        System.out.printf(String.join("\n",
            "You are about to produce %.2f of: %s",
            "Time required: %d",
            "Are you sure ([y]/n)? "
        ), amount, fp, fp.getProductionTime());
        INPUT.nextLine(); // Flush
        if (INPUT.nextLine().toLowerCase().equals("n")) {
            return;
        }
        
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        try {
            ObjectMessage msg = context.createObjectMessage(fp);
            msg.setFloatProperty("amount", amount);
            producer.send(newProductsQueue, msg);
        } catch (JMSException e) {
            System.err.println("Error while sending new products: " + e.getMessage());
        }
        
        context.close();
    }
    
    public void changeMSRP() {
        ConsoleUtils.clear();
        System.out.print(String.join("\n",
            "*** CHANGE MSRP ***",
            "Product id: "
        ));
        int id = INPUT.nextInt();

        EntityManager em = EMF.createEntityManager();

        FProduct fp = em.find(FProduct.class, id);
        if (fp == null) {
            System.err.println("Product " + id + " doesn't exist");
            em.close();
            return;
        }

        System.out.println("Current MSRP: " + fp.getMsrp());
        System.out.print("New MSRP? ");
        BigDecimal msrp = INPUT.nextBigDecimal();
        if (msrp.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("MSRP cannot be 0 or less");
            em.close();
            return;
        }

        em.getTransaction().begin();
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();

        fp.setMsrp(msrp);

        try {
            ObjectMessage msg = context.createObjectMessage(msrp);
            msg.setIntProperty("idProduct", id);
            producer.send(newPriceQueue, msg);

            em.getTransaction().commit();
        } catch (JMSException e) {
            System.err.println("Error while sending new price: " + e.getMessage());

            em.getTransaction().rollback();
        }

        context.close();
        em.close();
    }
    
    public void updateSpec() {
        // TODO
    }
}
