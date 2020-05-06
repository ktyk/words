package org.planetearth.words.controller.dto;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Data Transfer Object.
 *
 * @author katsuyuki.t
 */
@Data
public class WordEntryForm {

    /**
     * notation(表記)
     */
    @NotBlank
    private String notation;
    /**
     * reading(読み)
     */
    @NotBlank
    private String reading;
    /**
     * conversion(変換)
     */
    @NotBlank
    private String conversion;
    /**
     * abbreviation(略称)
     */
    private String abbreviation;
    /**
     * note(覚え書き)
     */
    private String note;
    /**
     * コンテキストid
     */
    @NotNull
    private Long contextId;
}
