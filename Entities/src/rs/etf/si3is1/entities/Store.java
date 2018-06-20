/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.si3is1.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Konstantin
 */
@Entity
@Table(name = "store")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Store.findAll", query = "SELECT s FROM Store s")
    , @NamedQuery(name = "Store.findByIdStore", query = "SELECT s FROM Store s WHERE s.idStore = :idStore")
    , @NamedQuery(name = "Store.findByName", query = "SELECT s FROM Store s WHERE s.name = :name")
    , @NamedQuery(name = "Store.findByAddress", query = "SELECT s FROM Store s WHERE s.address = :address")})
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_store")
    private Integer idStore;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idStore")
    private List<Reservation> reservationList;
    @JoinColumn(name = "id_manager", referencedColumnName = "id_manager")
    @ManyToOne(optional = false)
    private Manager idManager;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idStore")
    private List<Employee> employeeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idStore")
    private List<Revenue> revenueList;

    public Store() {
    }

    public Store(Integer idStore) {
        this.idStore = idStore;
    }

    public Store(Integer idStore, String name) {
        this.idStore = idStore;
        this.name = name;
    }

    public Integer getIdStore() {
        return idStore;
    }

    public void setIdStore(Integer idStore) {
        this.idStore = idStore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlTransient
    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    public Manager getIdManager() {
        return idManager;
    }

    public void setIdManager(Manager idManager) {
        this.idManager = idManager;
    }

    @XmlTransient
    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @XmlTransient
    public List<Revenue> getRevenueList() {
        return revenueList;
    }

    public void setRevenueList(List<Revenue> revenueList) {
        this.revenueList = revenueList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idStore != null ? idStore.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Store)) {
            return false;
        }
        Store other = (Store) object;
        if ((this.idStore == null && other.idStore != null) || (this.idStore != null && !this.idStore.equals(other.idStore))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (address != null)
                ? String.format("[%d] %s, %s", idStore, name, address)
                : String.format("[%d] %s", idStore, name);
    }
    
}
