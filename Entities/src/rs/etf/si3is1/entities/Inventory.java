/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.si3is1.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Konstantin
 */
@Entity
@Table(name = "inventory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Inventory.findAll", query = "SELECT i FROM Inventory i")
    , @NamedQuery(name = "Inventory.findByIdStore", query = "SELECT i FROM Inventory i WHERE i.inventoryPK.idStore = :idStore")
    , @NamedQuery(name = "Inventory.findByIdProduct", query = "SELECT i FROM Inventory i WHERE i.inventoryPK.idProduct = :idProduct")
    , @NamedQuery(name = "Inventory.findByAmount", query = "SELECT i FROM Inventory i WHERE i.amount = :amount")})
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected InventoryPK inventoryPK;
    @Basic(optional = false)
    @Column(name = "amount")
    private float amount;

    public Inventory() {
    }

    public Inventory(InventoryPK inventoryPK) {
        this.inventoryPK = inventoryPK;
    }

    public Inventory(InventoryPK inventoryPK, float amount) {
        this.inventoryPK = inventoryPK;
        this.amount = amount;
    }

    public Inventory(int idStore, int idProduct) {
        this.inventoryPK = new InventoryPK(idStore, idProduct);
    }

    public InventoryPK getInventoryPK() {
        return inventoryPK;
    }

    public void setInventoryPK(InventoryPK inventoryPK) {
        this.inventoryPK = inventoryPK;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (inventoryPK != null ? inventoryPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Inventory)) {
            return false;
        }
        Inventory other = (Inventory) object;
        if ((this.inventoryPK == null && other.inventoryPK != null) || (this.inventoryPK != null && !this.inventoryPK.equals(other.inventoryPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rs.etf.si3is1.entities.Inventory[ inventoryPK=" + inventoryPK + " ]";
    }
    
}
