package org.planetearth.words.controller.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;

/**
 * Data Transfer Object.
 *
 * @author katsuyuki.t
 */
@Data
public class ParagraphEntryForm {

    /**
     * title(表題)
     */
    private String title;
    /**
     * text(文章)
     */
    private String text;
    /**
     * remarks(補足)
     */
    private String remarks;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
