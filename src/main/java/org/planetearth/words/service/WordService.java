package org.planetearth.words.service;

import java.util.List;

import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.controller.dto.WordEntryForm;
import org.planetearth.words.domain.Word;

/**
 * Paragraph Service.
 *
 * @author katsuyuki.t
 */
public interface WordService {

    /**
     * Get a Word.
     *
     * @return Word Domain
     */
    Word findById(Long id);

    /**
     * Get a Word.
     *
     * @param contextId Context ID
     * @param notation  Notation
     * @return Word Domain
     */
    Word findByUniqueKey(Long contextId, String notation);

    /**
     * Get all Word.
     *
     * @return Word Domain List
     */
    List<Word> findAll();

    /**
     * Find Context's Subordinates.
     *
     * @return Word Domain List
     */
    List<Word> findSubordinate(Long contextId);

    /**
     * Find Word by Words (partial match search).
     *
     * @param searchForm including searching words
     * @return Word Domain List
     */
    List<Word> findBySearchForm(TextSearchForm searchForm);

    /**
     * Register a Word.
     *
     * @param form Entry
     */
    void register(WordEntryForm form);

    /**
     * Update Word.
     *
     * @param word Updater
     */
    void update(Word word);

    /**
     * Remove Word.
     *
     * @param id id
     */
    void remove(Long id);
}
