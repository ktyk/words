package org.planetearth.words.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.controller.dto.WordEntryForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Word;
import org.planetearth.words.repository.ContextRepository;
import org.planetearth.words.repository.WordRepository;
import org.planetearth.words.service.ContextService;
import org.planetearth.words.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link org.planetearth.words.service.WordService}.
 *
 * @author katsuyuki.t
 */
@Service
@Getter
@Setter
public class WordServiceImpl implements WordService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ContextService contextService;

    @Override
    public Word findById(Long id) {
        Word word = wordRepository.findById(id).orElse(null);
        if (word != null) {
            word.getContext().prepare();
            word.setContextId(word.getContext().getId());
        }
        return word;
    }

    @Override
    public List<Word> findAll() {
        return wordRepository.findAll(Sort.by(Order.asc("reading")));
    }

    @Override
    public List<Word> findBySearchForm(TextSearchForm searchForm) {
        List<String> searchWords = searchForm.parse();
        Set<Long> contextIds = contextService.collectIncludeIds(searchForm.getContextId());
        if (CollectionUtils.isEmpty(searchWords) && CollectionUtils.isEmpty(contextIds)) {
            return findAll();
        }
        if (CollectionUtils.isEmpty(searchWords)) {
            return wordRepository.findContainsWord(contextIds);
        }
        return CollectionUtils.isEmpty(contextIds) ? find(searchWords)
            : find(contextIds, searchWords);
    }

    private List<Word> find(List<String> searchWords) {
        List<Word> results = wordRepository.findContainsWord(searchWords.get(0));
        if (!CollectionUtils.isEmpty(results)) {
            Set<Long> exists = results.stream().map(Word::getId).collect(Collectors.toSet());
            results.forEach(w -> exists.add(w.getId()));
            for (String word : searchWords.subList(1, searchWords.size())) {
                // reset results
                results = wordRepository.findContainsWord(word, Lists.newArrayList(exists));
                results.forEach(w -> exists.add(w.getId()));
            }
        }
        return results;
    }

    private List<Word> find(Set<Long> contextIds, List<String> searchWords) {
        List<Word> results = wordRepository.findContainsWord(contextIds, searchWords.get(0));
        if (!CollectionUtils.isEmpty(results)) {
            Set<Long> exists = Sets.newHashSet();
            results.forEach(w -> exists.add(w.getId()));
            for (String word : searchWords.subList(1, searchWords.size())) {
                results = wordRepository
                    .findContainsWord(contextIds, word, Lists.newArrayList(exists));
                results.forEach(w -> exists.add(w.getId()));
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void register(WordEntryForm form) {
        Word word = new Word();
        word.setNotation(form.getNotation());
        word.setReading(form.getReading());
        word.setConversion(form.getConversion());
        word.setAbbreviation(form.getAbbreviation());
        word.setNote(form.getNote());
        Context context = contextRepository.findById(form.getContextId()).orElse(null);
        word.setContext(context);
        wordRepository.save(word);
    }

    @Override
    @Transactional
    public void update(Word word) {
        Word stored = wordRepository.findById(word.getId()).orElse(null);
        if (stored != null) {
            stored.setNotation(word.getNotation());
            stored.setReading(word.getReading());
            stored.setConversion(word.getConversion());
            stored.setAbbreviation(word.getAbbreviation());
            stored.setNotation(word.getNote());
            Context context = contextRepository.findById(word.getContextId()).orElse(null);
            word.setContext(context);
            wordRepository.save(word);
        }
    }

    @Override
    @Transactional
    public void remove(Long id) {
        Word word = wordRepository.getOne(id);
        word.getTerms().forEach(t -> t.getWords().remove(word));
        word.getTerms().clear();
        wordRepository.delete(word);
    }

    @Override
    public List<Word> findSubordinate(Long contextId) {
        TextSearchForm searchForm = new TextSearchForm();
        searchForm.setContextId(contextId);
        return findBySearchForm(searchForm);
    }

    @Override
    public Word findByUniqueKey(Long contextId, String notation) {
        return wordRepository.findByNotation(contextId, notation);
    }
}
