package org.planetearth.words.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * Term (用語)
 *
 * @author katsuyuki.t
 */
@Entity
@Table(name = "term", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "context_id"}))
@Getter
@Setter
public class Term extends Persistency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 名称
     */
    @Column
    private String name;
    /**
     * 説明
     */
    @Column(length = 4069)
    private String explanation;
    /**
     * 読み
     */
    @Column
    private String reading;
    /**
     * 訳
     */
    @Column
    private String translation;
    /**
     * 文脈
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", referencedColumnName = "id")
    private Context context;
    /**
     * 単語の構成
     */
    @ManyToMany(mappedBy = "terms")
    private List<Word> words;
    /**
     * 用語の使用先
     */
    @ManyToMany(mappedBy = "glossary")
    private Set<Paragraph> paragraphs = Sets.newHashSet();
    /**
     * 文脈id
     */
    @Transient
    private Long contextId;
    /**
     * 単語idリスト
     */
    @Transient
    private Set<Long> wordIds;
    /**
     * 説明冒頭
     */
    @Transient
    private String headOfExplanation;
    /**
     * リソース(パラグラフidの連結として表現)
     */
    @Transient
    private String resource;

    public void cutExplanation(Integer length) {
        if (length == null) {
            headOfExplanation = explanation;
        } else if (explanation != null) {
            headOfExplanation = StringUtils.left(explanation, length);
        }
    }

    public Set<Long> extractParagraphs() {
        if (StringUtils.isNotEmpty(resource)) {
            return Arrays.stream(StringUtils.split(resource, ","))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        }
        return Sets.newHashSet();
    }

    public void flattenParagraphs() {
        StringBuffer buff = new StringBuffer();
        paragraphs.forEach(p -> buff.append(p.getId().toString()).append(","));
        resource = StringUtils.stripEnd(buff.toString(), ",");
    }
}
