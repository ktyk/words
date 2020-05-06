package org.planetearth.words.controller.dto;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Data Transfer Object.
 *
 * @author katsuyuki.t
 */
@Data
public class TermEntryForm {

    /**
     * name(名称)
     */
    @NotBlank
    private String name;
    /**
     * reading(読み)
     */
    @NotBlank
    private String reading;
    /**
     * translation(訳)
     */
    private String translation;
    /**
     * explanation(説明)
     */
    private String explanation;
    /**
     * resource(リソース)
     */
    private String resource;
    /**
     * コンテキストid
     */
    @NotNull
    private Long contextId;
    /**
     * 単語セット
     */
    private Set<Long> wordIds;

    public Set<Long> extractParagraphs() {
        if (StringUtils.isNotEmpty(resource)) {
            return Arrays.stream(StringUtils.split(resource, ",")).map(Long::valueOf)
                .collect(Collectors.toSet());
        }
        return Sets.newHashSet();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
