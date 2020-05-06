package org.planetearth.words.service;

import java.util.List;
import java.util.Set;

import org.planetearth.words.controller.dto.ContextEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Context;

/**
 * Context Service.
 *
 * @author katsuyuki.t
 */
public interface ContextService {

    /**
     * Get a Context.
     *
     * @param id Context Id
     * @return Context Domain
     */
    Context findById(Long id);

    /**
     * Get all Contexts.
     *
     * @return Context Domain List
     */
    List<Context> findAll();

    /**
     * Find Context by Words.
     * partial match search.
     *
     * @param searchForm including searching words
     * @return Context Domain List
     */
    List<Context> findBySearchForm(TextSearchForm searchForm);

    /**
     * Register a Context.
     *
     * @param form Entry
     */
    void register(ContextEntryForm form);

    /**
     * Update Context.
     *
     * @param updater Context Domain
     */
    void update(Context updater);

    /**
     * Remove Context.
     *
     * @param id Context Id
     */
    void remove(Long id);

    /**
     * Checking Referrer.
     *
     * @param id Context Id
     * @return True or False
     */
    Boolean existsReferrer(Long id);

    /**
     * Checking Already Resisted.
     *
     * @param parentContextName SuperOrdinate Context Name
     * @param contextName Context Name
     * @return True or False
     */
    Boolean existsSameNameContext(String parentContextName, String contextName);

    /**
     *
     * @param contextId Context Id
     * @return Context Domain List
     */
    List<Context> findSuperordinate(Long contextId);

    /**
     * Find Context's Subordinates.
     *
     * @param contextId Context Id
     * @return Context Domain List
     */
    List<Context> findSubordinate(Long contextId);

    /**
     * Collect Self and Children Id.
     *
     * @param contextId Context Id
     * @return All id
     */
    Set<Long> collectIncludeIds(Long contextId);
}
