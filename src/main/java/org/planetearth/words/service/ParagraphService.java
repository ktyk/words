package org.planetearth.words.service;

import java.util.List;

import org.planetearth.words.controller.dto.ParagraphEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Paragraph;

/**
 * Paragraph Service.
 *
 * @author katsuyuki.t
 */
public interface ParagraphService {

    /**
     * Get a Paragraph.
     *
     * @return Paragraph Domain
     */
    Paragraph findById(Long id);

    /**
     * Get all Paragraph.
     *
     * @return Paragraph Domain List
     */
    List<Paragraph> findAll();

    /**
     * Find Paragraph by Words (partial match search).
     *
     * @param searchForm including searching words
     * @return Paragraph Domain List
     */
    List<Paragraph> findBySearchForm(TextSearchForm searchForm);

    /**
     * Register a Paragraph.
     *
     * @param form Entry
     */
    void register(ParagraphEntryForm form);

    /**
     * Update Paragraph.
     *
     * @param paragraph Paragraph
     */
    void update(Paragraph paragraph);

    /**
     * Remove Paragraph.
     *
     * @param id id
     */
    void remove(Long id);
}
