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
@Table(name = "turnover")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Turnover.findAll", query = "SELECT t FROM Turnover t")
    , @NamedQuery(name = "Turnover.findByIdTurnover", query = "SELECT t FROM Turnover t WHERE t.idTurnover = :idTurnover")
    , @NamedQuery(name = "Turnover.findByDate", query = "SELECT t FROM Turnover t WHERE t.date = :date")
    , @NamedQuery(name = "Turnover.findByAmount", query = "SELECT t FROM Turnover t WHERE t.amount = :amount")
    , @NamedQuery(name = "Turnover.findByProfit", query = "SELECT t FROM Turnover t WHERE t.profit = :profit")})
public class Turnover implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_turnover")
    private Integer idTurnover;
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

    public Turnover() {
    }

    public Turnover(Integer idTurnover) {
        this.idTurnover = idTurnover;
    }

    public Turnover(Integer idTurnover, Date date, float amount, BigDecimal profit) {
        this.idTurnover = idTurnover;
        this.date = date;
        this.amount = amount;
        this.profit = profit;
    }

    public Integer getIdTurnover() {
        return idTurnover;
    }

    public void setIdTurnover(Integer idTurnover) {
        this.idTurnover = idTurnover;
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
        hash += (idTurnover != null ? idTurnover.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Turnover)) {
            return false;
        }
        Turnover other = (Turnover) object;
        if ((this.idTurnover == null && other.idTurnover != null) || (this.idTurnover != null && !this.idTurnover.equals(other.idTurnover))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rs.etf.si3is1.entities.Turnover[ idTurnover=" + idTurnover + " ]";
    }
    
}
