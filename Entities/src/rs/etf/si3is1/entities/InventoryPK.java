/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.si3is1.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Konstantin
 */
@Embeddable
public class InventoryPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id_store")
    private int idStore;
    @Basic(optional = false)
    @Column(name = "id_product")
    private int idProduct;

    public InventoryPK() {
    }

    public InventoryPK(int idStore, int idProduct) {
        this.idStore = idStore;
        this.idProduct = idProduct;
    }

    public int getIdStore() {
        return idStore;
    }

    public void setIdStore(int idStore) {
        this.idStore = idStore;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idStore;
        hash += (int) idProduct;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InventoryPK)) {
            return false;
        }
        InventoryPK other = (InventoryPK) object;
        if (this.idStore != other.idStore) {
            return false;
        }
        if (this.idProduct != other.idProduct) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rs.etf.si3is1.entities.InventoryPK[ idStore=" + idStore + ", idProduct=" + idProduct + " ]";
    }
    
}
