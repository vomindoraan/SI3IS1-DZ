/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.si3is1.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Konstantin
 */
@Entity
@Table(name = "revenue")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Revenue.findAll", query = "SELECT r FROM Revenue r")
    , @NamedQuery(name = "Revenue.findByIdRevenue", query = "SELECT r FROM Revenue r WHERE r.idRevenue = :idRevenue")
    , @NamedQuery(name = "Revenue.findByDate", query = "SELECT r FROM Revenue r WHERE r.date = :date")
    , @NamedQuery(name = "Revenue.findByAmount", query = "SELECT r FROM Revenue r WHERE r.amount = :amount")
    , @NamedQuery(name = "Revenue.findByProfit", query = "SELECT r FROM Revenue r WHERE r.profit = :profit")})
public class Revenue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_revenue")
    private Integer idRevenue;
    @Basic(optional = false)
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Basic(optional = false)
    @Column(name = "amount")
    private float amount;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "profit")
    private BigDecimal profit;
    @JoinColumn(name = "id_store", referencedColumnName = "id_store")
    @ManyToOne(optional = false)
    private Store idStore;

    public Revenue() {
    }

    public Revenue(Integer idRevenue) {
        this.idRevenue = idRevenue;
    }

    public Revenue(Integer idRevenue, Date date, float amount, BigDecimal profit) {
        this.idRevenue = idRevenue;
        this.date = date;
        this.amount = amount;
        this.profit = profit;
    }

    public Integer getIdRevenue() {
        return idRevenue;
    }

    public void setIdRevenue(Integer idRevenue) {
        this.idRevenue = idRevenue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Store getIdStore() {
        return idStore;
    }

    public void setIdStore(Store idStore) {
        this.idStore = idStore;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRevenue != null ? idRevenue.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Revenue)) {
            return false;
        }
        Revenue other = (Revenue) object;
        if ((this.idRevenue == null && other.idRevenue != null) || (this.idRevenue != null && !this.idRevenue.equals(other.idRevenue))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rs.etf.si3is1.entities.Revenue[ idRevenue=" + idRevenue + " ]";
    }
    
}
