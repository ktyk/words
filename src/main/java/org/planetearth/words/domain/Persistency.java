package org.planetearth.words.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;

/**
 * Persistence Support.
 *
 * @author katsuyuki.t
 */
@MappedSuperclass
@Getter
@Setter
public abstract class Persistency {

    @Basic(optional = false)
    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    protected Date lastUpdated;

    @PrePersist
    public void prePersist() {
        lastUpdated = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = new Date();
    }
}
