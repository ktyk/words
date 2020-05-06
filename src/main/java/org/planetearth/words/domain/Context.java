package org.planetearth.words.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

/**
 * Context(背景・文脈)
 *
 * @author katsuyuki.t
 */
@Entity
@Table(name = "context", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_name"}))
@Getter
@Setter
public class Context extends Persistency {

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
     * 親コンテキストパス（idの「.」連結)
     */
    @Column(length = 1024, name = "parent_path")
    private String parentPath;
    /**
     * 親コンテキスト名称（名称の「/」連結）
     */
    @Column(length = 1024, name = "parent_name")
    private String parentName;
    /**
     * 親コンテキスト
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Context parent;
    /**
     * 子コンテキスト
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Context> children;
    /**
     * パス
     */
    @Transient
    private String currentPath;
    /**
     * パス名称
     */
    @Transient
    private String currentPathName;

    public void prepare() {
        currentPath = resolveFullPath();
        currentPathName = resolveFullPathName();
    }

    public String resolveFullPath() {
        if (id != null && parent != null && parent.getId() != null) {
            return parentPath + "." + id.toString();
        }
        return id != null ? id.toString() : null;
    }

    public String resolveFullPathName() {
        StringBuilder sb = new StringBuilder();
        if (parentName == null) {
            sb.append("/");
        } else if (parentName.equals("/")) {
            sb.append(parentName).append(name);
        } else {
            sb.append(parentName).append("/").append(name);
        }
        return sb.toString();
    }
}
