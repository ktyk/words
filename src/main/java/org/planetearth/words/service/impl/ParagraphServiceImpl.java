package org.planetearth.words.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.planetearth.words.controller.dto.ParagraphEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Paragraph;
import org.planetearth.words.repository.ParagraphRepository;
import org.planetearth.words.service.ParagraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link org.planetearth.words.service.ParagraphService}.
 *
 * @author katsuyuki.t
 */
@Service
@Getter
@Setter
public class ParagraphServiceImpl implements ParagraphService {

    @Autowired
    private ParagraphRepository paragraphRepository;

    private final Integer width = 100;

    @Override
    public Paragraph findById(Long id) {
        return paragraphRepository.findById(id).orElse(null);
    }

    @Override
    public List<Paragraph> findAll() {
        List<Paragraph> results = paragraphRepository.findAll(Sort.by(Order.desc("lastUpdated")));
        results.forEach(p -> p.cutText(width));
        return results;
    }

    @Override
    public List<Paragraph> findBySearchForm(TextSearchForm searchForm) {
        if (CollectionUtils.isEmpty(searchForm.parse())) {
            return findAll();
        }
        return find(searchForm.parse());
    }

    private List<Paragraph> find(List<String> words) {
        List<Paragraph> results = paragraphRepository.findContainsWord(words.get(0));
        if (!CollectionUtils.isEmpty(results)) {
            Set<Long> exists = results.stream().map(Paragraph::getId).collect(Collectors.toSet());
            results.forEach(p -> exists.add(p.getId()));
            for (String word : words.subList(1, words.size())) {
                // reset results
                results = paragraphRepository.findContainsWord(word, Lists.newArrayList(exists));
                results.forEach(p -> exists.add(p.getId()));
            }
        }
        results.forEach(p -> p.cutText(width));
        return results;
    }

    @Override
    @Transactional
    public void register(ParagraphEntryForm form) {
        Paragraph paragraph = new Paragraph();
        paragraph.setTitle(form.getTitle());
        paragraph.setText(form.getText());
        paragraph.setRemarks(form.getRemarks());
        paragraphRepository.save(paragraph);
    }

    @Override
    @Transactional
    public void update(Paragraph paragraph) {
        paragraphRepository.save(paragraph);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        paragraphRepository.findById(id).ifPresent(p -> paragraphRepository.delete(p));
    }
}
