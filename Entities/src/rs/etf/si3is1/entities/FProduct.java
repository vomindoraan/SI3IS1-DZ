/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.si3is1.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Konstantin
 */
@Entity
@Table(name = "f_product", catalog = "si3is1_factory", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FProduct.findAll", query = "SELECT f FROM FProduct f")
    , @NamedQuery(name = "FProduct.findByIdFProduct", query = "SELECT f FROM FProduct f WHERE f.idFProduct = :idFProduct")
    , @NamedQuery(name = "FProduct.findByName", query = "SELECT f FROM FProduct f WHERE f.name = :name")
    , @NamedQuery(name = "FProduct.findByType", query = "SELECT f FROM FProduct f WHERE f.type = :type")
    , @NamedQuery(name = "FProduct.findByMsrp", query = "SELECT f FROM FProduct f WHERE f.msrp = :msrp")
    , @NamedQuery(name = "FProduct.findByProductionTime", query = "SELECT f FROM FProduct f WHERE f.productionTime = :productionTime")})
public class FProduct implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_f_product")
    private Integer idFProduct;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "msrp")
    private BigDecimal msrp;
    @Basic(optional = false)
    @Column(name = "production_time")
    private int productionTime;

    public FProduct() {
    }

    public FProduct(Integer idFProduct) {
        this.idFProduct = idFProduct;
    }

    public FProduct(Integer idFProduct, String name, BigDecimal msrp, int productionTime) {
        this.idFProduct = idFProduct;
        this.name = name;
        this.msrp = msrp;
        this.productionTime = productionTime;
    }

    public Integer getIdFProduct() {
        return idFProduct;
    }

    public void setIdFProduct(Integer idFProduct) {
        this.idFProduct = idFProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getMsrp() {
        return msrp;
    }

    public void setMsrp(BigDecimal msrp) {
        this.msrp = msrp;
    }

    public int getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(int productionTime) {
        this.productionTime = productionTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFProduct != null ? idFProduct.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FProduct)) {
            return false;
        }
        FProduct other = (FProduct) object;
        if ((this.idFProduct == null && other.idFProduct != null) || (this.idFProduct != null && !this.idFProduct.equals(other.idFProduct))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s – ¤%.2f (t=%d)", idFProduct, name, msrp, productionTime);
    }
    
}
