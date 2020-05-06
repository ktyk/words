package org.planetearth.words.controller.dto;

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
public class ContextEntryForm {

    /**
     * name(名称)
     */
    @NotBlank
    private String name;
    /**
     * explanation(説明)
     */
    private String explanation;
    /**
     * parentName(親コンテキスト名)
     */
    private String parentName;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
