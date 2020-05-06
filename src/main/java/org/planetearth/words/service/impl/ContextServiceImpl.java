package org.planetearth.words.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.planetearth.words.controller.dto.ContextEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Term;
import org.planetearth.words.domain.Word;
import org.planetearth.words.repository.ContextRepository;
import org.planetearth.words.repository.TermRepository;
import org.planetearth.words.repository.WordRepository;
import org.planetearth.words.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link org.planetearth.words.service.ContextService}.
 *
 * @author katsuyuki.t
 */
@Service
@Getter
@Setter
public class ContextServiceImpl implements ContextService {

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private WordRepository wordRepository;

    @Override
    public Context findById(Long id) {
        Context context = contextRepository.findById(id).orElse(null);
        if (context != null) {
            context.prepare();
        }
        return context;
    }

    @Override
    public List<Context> findAll() {
        List<Context> contexts = contextRepository
            .findAll(Sort.by(Order.asc("parentName"), Order.asc("id")));
        contexts.forEach(Context::prepare);
        return contexts;
    }

    @Override
    public List<Context> findBySearchForm(TextSearchForm searchForm) {
        if (CollectionUtils.isEmpty(searchForm.parse())) {
            return findAll();
        }
        return find(searchForm.parse());
    }

    private List<Context> find(List<String> searchWords) {
        List<Context> results = contextRepository.findContainsName(searchWords.get(0));
        if (!results.isEmpty()) {
            Set<Long> exists = results.stream().map(Context::getId).collect(Collectors.toSet());
            for (String word : searchWords.subList(1, searchWords.size())) {
                // reset results
                results = contextRepository.findContainsName(word, Lists.newArrayList(exists));
                results.forEach(c -> exists.add(c.getId()));
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void register(ContextEntryForm form) {
        if (!StringUtils.isEmpty(form.getName())) {
            Context context = new Context();
            context.setParentName(form.getParentName());
            context.setName(form.getName());
            context.setExplanation(form.getExplanation());

            if (StringUtils.isNotBlank(context.getParentName())) {
                Context parent = contextRepository.findByName(form.getParentName());
                parent.prepare();
                context.setParentPath(parent.getCurrentPath());
                context.setParentName(parent.getCurrentPathName());
                context.setParent(parent);
            } else {
                context.setParentName("/");
            }
            contextRepository.save(context);
        }
    }

    @Override
    @Transactional
    public void update(Context context) {
        Context stored = contextRepository.findById(context.getId()).orElse(null);
        if (stored != null) {
            if (!stored.getName().equals(context.getName())) {
                List<Context> children = stored.getChildren();
                children.forEach(
                    c -> c.setParentName(stored.getParentName() + "/" + context.getName()));
            }
            stored.setName(context.getName());
            stored.setExplanation(context.getExplanation());
            contextRepository.save(stored);
        }
    }

    @Override
    @Transactional
    public void remove(Long id) {
        contextRepository.findById(id).ifPresent(c -> contextRepository.delete(c));
    }

    @Override
    public Boolean existsReferrer(Long id) {
        Context context = contextRepository.findById(id).orElse(null);
        if (context != null) {
            List<Term> terms = termRepository.findByContext(context);
            List<Word> words = wordRepository.findByContextId(id);
            return !CollectionUtils.isEmpty(terms) || !CollectionUtils.isEmpty(words);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean existsSameNameContext(String parentContextName, String contextName) {
        if (StringUtils.isEmpty(parentContextName)) {
            return contextRepository.findByName(contextName) != null;
        }
        final String parentPath = "/" + parentContextName;
        return contextRepository.findByNameWithParent(contextName, parentPath) != null;
    }


    @Override
    public List<Context> findSuperordinate(Long contextId) {
        List<Context> results = Lists.newArrayList();
        collectParents(contextId, results);
        return results;
    }

    private void collectParents(Long contextId, List<Context> parents) {
        Context context = contextRepository.findById(contextId).orElse(null);
        if (context != null) {
            parents.add(context);
            if (context.getParent() != null) {
                collectParents(context.getParent().getId(), parents);
            }
        }
    }

    @Override
    public List<Context> findSubordinate(Long contextId) {
        List<Context> results = Lists.newArrayList();
        Context basis = contextRepository.findById(contextId).orElse(null);
        if (basis != null) {
            results.add(basis);
            retrieve(basis, results);
        }
        return results;
    }

    private void retrieve(Context context, List<Context> subordinate) {
        if (context != null && !CollectionUtils.isEmpty(context.getChildren())) {
            subordinate.addAll(context.getChildren());
            contextRepository.findById(context.getId())
                .ifPresent(c -> c.getChildren().forEach(c2 -> retrieve(c2, subordinate)));
        }
    }

    @Override
    public Set<Long> collectIncludeIds(Long contextId) {
        Set<Long> contextIds = Sets.newHashSet();
        if (contextId != null) {
            contextIds.add(contextId);
            collectChildren(contextId, contextIds);
        }
        return contextIds;
    }

    private void collectChildren(Long contextId, Set<Long> childrenIds) {
        Context context = contextRepository.findById(contextId).orElse(null);
        if (context != null) {
            context.getChildren().forEach(c -> childrenIds.add(c.getId()));
            context.getChildren().forEach(c -> collectChildren(c.getId(), childrenIds));
        }
    }
}
