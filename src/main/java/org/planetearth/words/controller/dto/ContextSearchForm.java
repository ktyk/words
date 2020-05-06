package org.planetearth.words.controller.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Data Transfer Object.
 *
 * @author katsuyuki.t
 */
@Data
public class ContextSearchForm {

    /**
     * context id
     */
    @NotNull
    private Long contextId;
}
