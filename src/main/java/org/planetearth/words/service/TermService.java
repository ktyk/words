package org.planetearth.words.service;

import java.util.List;
import java.util.Set;

import org.planetearth.words.controller.dto.TermEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Term;

/**
 * Paragraph Service.
 *
 * @author katsuyuki.t
 */
public interface TermService {

    /**
     * Get a Term.
     *
     * @return Term Domain
     */
    Term findById(Long id);

    /**
     * Get a Term.
     *
     * @param contextId Context ID
     * @param name      Name
     * @return Term Domain
     */
    Term findByUniqueKey(Long contextId, String name);

    /**
     * Get all Term.
     *
     * @return Term Domain List
     */
    List<Term> findAll();

    /**
     * Find Term by Words<br> partial match search.
     *
     * @param searchForm including searching words
     * @return Term Domain List
     */
    List<Term> findBySearchForm(TextSearchForm searchForm);

    /**
     * Register a Term.
     *
     * @param form Entry
     */
    void register(TermEntryForm form);

    /**
     * Update Term.
     *
     * @param term Updater
     */
    void update(Term term);

    /**
     * Remove Term.
     *
     * @param id id
     */
    void remove(Long id);

    /**
     * Translate Word.
     *
     * @param contextId contextId
     * @param word      original
     * @param wordIds   word id list(results)
     * @return translated word
     */
    String translate(Long contextId, String word, Set<Long> wordIds);
}
