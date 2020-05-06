package org.planetearth.words.service.impl;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.planetearth.words.controller.dto.TermEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Paragraph;
import org.planetearth.words.domain.Term;
import org.planetearth.words.domain.Word;
import org.planetearth.words.repository.ContextRepository;
import org.planetearth.words.repository.ParagraphRepository;
import org.planetearth.words.repository.TermRepository;
import org.planetearth.words.repository.WordRepository;
import org.planetearth.words.service.ContextService;
import org.planetearth.words.service.TermService;
import org.planetearth.words.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link org.planetearth.words.service.TermService}.
 *
 * @author katsuyuki.t
 */
@Service
@Getter
@Setter
public class TermServiceImpl implements TermService {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private ContextService contextService;

    private final Integer width = 40;

    @Override
    public Term findById(Long id) {
        Term term = termRepository.findById(id).orElse(null);
        if (term != null) {
            term.getContext().prepare();
            term.setContextId(term.getContext().getId());
        }
        return term;
    }

    @Override
    public List<Term> findAll() {
        List<Term> results = termRepository.findAll(Sort.by(Order.asc("reading")));
        results.forEach(e -> e.cutExplanation(width));
        return results;
    }

    @Override
    public List<Term> findBySearchForm(TextSearchForm searchForm) {
        List<String> searchWords = searchForm.parse();
        Set<Long> contextIds = contextService.collectIncludeIds(searchForm.getContextId());
        if (CollectionUtils.isEmpty(searchWords) && CollectionUtils.isEmpty(contextIds)) {
            return findAll();
        }
        if (CollectionUtils.isEmpty(searchWords)) {
            List<Term> results = termRepository.findContainsWord(contextIds);
            results.forEach(t -> t.cutExplanation(width));
            return results;
        }
        return CollectionUtils.isEmpty(contextIds) ? find(searchWords)
            : find(contextIds, searchWords);
    }

    private List<Term> find(List<String> searchWords) {
        List<Term> results = termRepository.findContainsWord(searchWords.get(0));
        if (!CollectionUtils.isEmpty(results)) {
            Set<Long> exists = results.stream().map(Term::getId).collect(Collectors.toSet());
            for (String word : searchWords.subList(1, searchWords.size())) {
                // reset results
                results = termRepository.findContainsWord(word, Lists.newArrayList(exists));
                results.forEach(p -> exists.add(p.getId()));
            }
        }
        results.forEach(t -> t.cutExplanation(width));
        return results;
    }

    private List<Term> find(Set<Long> contextIds, List<String> searchWords) {
        List<Term> results = termRepository.findContainsWord(contextIds, searchWords.get(0));
        if (!CollectionUtils.isEmpty(results)) {
            Set<Long> exists = results.stream().map(Term::getId).collect(Collectors.toSet());
            for (String word : searchWords.subList(1, searchWords.size())) {
                // results are filtered each finding
                results = termRepository
                    .findContainsWord(contextIds, word, Lists.newArrayList(exists));
                results.forEach(p -> exists.add(p.getId()));
            }
        }
        results.forEach(t -> t.cutExplanation(width));
        return results;
    }

    @Override
    @Transactional
    public void register(TermEntryForm form) {
        Term term = new Term();
        term.setName(form.getName());
        term.setExplanation(form.getExplanation());
        term.setReading(form.getReading());
        term.setTranslation(form.getTranslation());
        term.setContext(contextRepository.findById(form.getContextId()).orElse(null));
        if (!CollectionUtils.isEmpty(form.getWordIds())) {
            List<Word> words = wordRepository.findByIds(form.getWordIds());
            term.setWords(words);
            termRepository.save(term);
            words.forEach(w -> storeWord(term, w));
        } else {
            termRepository.save(term);
        }
        relateParagraph(form.extractParagraphs(), term);
    }

    private void storeWord(Term term, Word word) {
        word.getTerms().add(term);
        wordRepository.save(word);
    }

    private void relateParagraph(Set<Long> enteringIds, Term stored) {
        if (isThereDifference(enteringIds, stored.extractParagraphs())) {
            Set<Paragraph> relateParagraphs = new HashSet<>(paragraphRepository
                .findByIds(stored.extractParagraphs()));
            relateParagraphs
                .forEach(p -> p.setGlossary(rebuildGlossary(p.getGlossary(), stored.getId())));
            stored.getParagraphs().forEach(p -> p.getGlossary().add(stored));
            relateParagraphs.forEach(p -> storeParagraph(stored, p));
            stored.setParagraphs(relateParagraphs);
            stored.flattenParagraphs();
        }
    }

    private Boolean isThereDifference(Set<Long> paragraphIds, Set<Long> storedIds) {
        if (CollectionUtils.isEmpty(storedIds) && CollectionUtils.isEmpty(paragraphIds)) {
            return Boolean.FALSE;
        }
        if (!CollectionUtils.isEmpty(storedIds) && !CollectionUtils.isEmpty(paragraphIds)) {
            if (storedIds.size() == paragraphIds.size() && paragraphIds.containsAll(storedIds)) {
                return Boolean.FALSE;
            }
        }
        return CollectionUtils.isEmpty(storedIds) ? Boolean.FALSE : Boolean.TRUE;
    }

    private Set<Term> rebuildGlossary(Set<Term> terms, Long termId) {
        return terms.stream().filter(t -> !(t.getId().equals(termId))).collect(Collectors.toSet());
    }

    private void storeParagraph(Term term, Paragraph paragraph) {
        paragraph.getGlossary().add(term);
        paragraphRepository.save(paragraph);
    }

    @Override
    @Transactional
    public void update(Term term) {
        Term stored = termRepository.findById(term.getId()).orElse(null);
        if (stored != null) {
            restoreParagraph(term, stored);
            stored.setName(term.getName());
            stored.setExplanation(term.getExplanation());
            stored.setReading(term.getReading());
            stored.setTranslation(term.getTranslation());
            Context context = contextRepository.findById(term.getContextId()).orElse(null);
            stored.setContext(context);
            termRepository.save(stored);
        }
    }

    private void restoreParagraph(Term updater, Term stored) {
        // current and renewal and these difference
        Set<Long> currentParagraphIds = stored.getParagraphs().stream().map(Paragraph::getId)
            .collect(Collectors.toSet());
        Set<Long> renewalParagraphIds = updater.extractParagraphs();
        Set<Long> appendParagraphIds = renewalParagraphIds.stream()
            .filter(p -> !(currentParagraphIds.contains(p)))
            .collect(Collectors.toSet());
        Set<Long> eraseParagraphIds = currentParagraphIds.stream()
            .filter(p -> !(renewalParagraphIds.contains(p)))
            .collect(Collectors.toSet());

        Set<Paragraph> restore = stored.getParagraphs().stream()
            .filter(p -> !(eraseParagraphIds.contains(p.getId())))
            .collect(Collectors.toSet());
        List<Paragraph> appended = appendParagraphIds.isEmpty() ? Lists.newArrayList()
            : paragraphRepository.findByIds(appendParagraphIds);
        appended.forEach(a -> a.getGlossary().add(updater));
        restore.addAll(Sets.newHashSet(appended));
        stored.setParagraphs(restore);
        stored.flattenParagraphs();
        if (!eraseParagraphIds.isEmpty()) {
            releaseTerm(eraseParagraphIds, stored.getId());
        }
    }

    private void releaseTerm(Set<Long> eraseParagraphIds, Long termId) {
        List<Paragraph> paragraphs = paragraphRepository.findByIds(eraseParagraphIds);
        paragraphs.forEach(p -> eraseGlossary(p.getGlossary(), termId));
    }

    private void eraseGlossary(Set<Term> terms, Long termId) {
        terms.removeIf(t -> t.getId().equals(termId));
    }

    @Override
    @Transactional
    public void remove(Long id) {
        Term term = termRepository.findById(id).orElse(null);
        if (term != null) {
            term.getWords().forEach(w -> w.getTerms().remove(term));
            term.getWords().clear();
            termRepository.delete(term);
        }
    }

    @Override
    public Term findByUniqueKey(Long contextId, String name) {
        return termRepository.findByName(contextId, name);
    }

    @Override
    public String translate(Long contextId, String word, Set<Long> wordIds) {
        List<Context> contexts = contextService.findSuperordinate(contextId);
        Collections.reverse(contexts);
        Map<String, String> dictionary = Maps.newHashMap();
        // overwrite parent to child.
        contexts.forEach(c -> compilation(c.getId(), dictionary));

        List<Pair<String, String>> conversions = Lists.newArrayList();
        for (int len = word.length(); len > 0; len--) {
            Pair<String, String> conversion = convert(dictionary, StringUtils.right(word, len));
            if (conversion != null) {
                len = len - conversion.getLeft().length() + 1; // not reduce one.
                conversions.add(conversion);
            }
        }
        return neoterize(word, conversions, contextId, wordIds);
    }

    private void compilation(Long contextId, Map<String, String> dictionary) {
        List<Word> words = wordService.findSubordinate(contextId);
        words.forEach(w -> dictionary.put(w.getNotation(), w.getConversion()));
    }

    private String neoterize(String word, List<Pair<String, String>> conversions, Long contextId,
        Set<Long> wordIds) {
        StringBuilder origin = new StringBuilder();
        StringBuilder translate = new StringBuilder();
        for (Pair<String, String> pair : conversions) {
            origin.append(pair.getLeft());
            translate
                .append(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, pair.getRight()));
        }
        if (word.equals(origin.toString())) {
            for (Pair<String, String> pair : conversions) {
                List<Context> subordinate = contextService.findSubordinate(contextId);
                Set<Long> contextIds = subordinate.stream().map(Context::getId)
                    .collect(Collectors.toSet());
                List<Word> stored = wordRepository.findByNotation(contextIds, pair.getLeft());
                if (CollectionUtils.isNotEmpty(stored)) {
                    // head one if exists multiple results.
                    wordIds.add(stored.get(0).getId());
                }
            }
            return translate.toString();
        } else if (conversions.isEmpty()) {
            return "* This Name Not Translate in Context *";
        } else {
            return "* Partial Match : " + conversions.toString();
        }
    }

    private Pair<String, String> convert(Map<String, String> dictionary, String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        } else {
            int len = word.length();
            String fragment = dictionary.get(StringUtils.left(word, len));
            if (fragment != null) {
                return Pair.of(StringUtils.left(word, len), fragment);
            }
            // recursion
            return convert(dictionary, StringUtils.left(word, --len));
        }
    }
}
