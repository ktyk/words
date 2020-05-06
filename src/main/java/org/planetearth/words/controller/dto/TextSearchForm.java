package org.planetearth.words.controller.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;

/**
 * Data Transfer Object.
 *
 * @author katsuyuki.t
 */
@Data
public class TextSearchForm {

    /**
     * search word
     */
    private String searching;
    /**
     * context id
     */
    private Long contextId;

    public List<String> parse() {
        return Arrays.asList(StringUtils.split(formalize(), " "));
    }

    private String formalize() {
        if (StringUtils.isNotEmpty(searching)) {
            return searching.replaceAll("\\p{javaWhitespace}", " ");
        }
        return "";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
