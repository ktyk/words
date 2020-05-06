package org.planetearth.words.domain;

import com.google.common.collect.Sets;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Getter;
import lombok.Setter;

/**
 * Word (単語)
 *
 * @author katsuyuki.t
 */
@Entity
@Table(name = "word", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"notation", "context_id"})})
@Getter
@Setter
public class Word extends Persistency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlAttribute(name = "id")
    private Long id;
    /**
     * 表記
     */
    @Column
    @XmlElement(name = "notation")
    private String notation;
    /**
     * 読み
     */
    @Column
    private String reading;
    /**
     * 変換
     */
    @Column
    private String conversion;
    /**
     * 略称
     */
    @Column
    private String abbreviation;
    /**
     * 覚え書き
     */
    @Column(length = 4069)
    @XmlTransient
    private String note;
    /**
     * 用語セット（この単語の利用先)
     */
    @ManyToMany
    @JoinTable(name = "term_word", joinColumns = {
        @JoinColumn(name = "word_id")}, inverseJoinColumns = {
        @JoinColumn(name = "term_id")})
    private Set<Term> terms = Sets.newHashSet();
    /**
     * 文脈
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", referencedColumnName = "id")
    @XmlTransient
    private Context context;
    /**
     * 文脈id
     */
    @Transient
    private Long contextId;
}
