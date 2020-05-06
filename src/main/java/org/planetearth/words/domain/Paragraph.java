package org.planetearth.words.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Paragraph (パラグラフ・段落)
 *
 * @author katsuyuki.t
 */
@Entity
@Table(name = "paragraph")
@Getter
@Setter
public class Paragraph extends Persistency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 表題
     */
    @Column
    private String title;
    /**
     * 文章
     */
    @Column(length = 4096)
    private String text;
    /**
     * 備考
     */
    @Column
    private String remarks;
    /**
     * 用語集
     */
    @ManyToMany
    @JoinTable(name = "term_paragraph", joinColumns = {
        @JoinColumn(name = "paragraph_id")}, inverseJoinColumns = {
        @JoinColumn(name = "term_id")})
    private Set<Term> glossary = Sets.newHashSet();
    /**
     * 文章冒頭
     */
    @Transient
    private String headOfText;

    public void cutText(Integer length) {
        if (length == null) {
            headOfText = text;
        } else if (text != null) {
            headOfText = StringUtils.left(text, length);
        }
    }

    public Set<Term> fetchGlossary() {
        glossary.forEach(t -> t.getContext().prepare());
        glossary.forEach(t -> t.cutExplanation(Integer.valueOf("50")));
        return glossary;
    }
}
