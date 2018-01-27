package rs.etf.si3is1.messaging;

public interface Identifiable {
    int getID();
    
    default String getIDString() {
        return this.getClass().getSimpleName() + getID();
    }
}
