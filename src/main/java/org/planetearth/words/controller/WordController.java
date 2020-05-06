package org.planetearth.words.controller;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.planetearth.words.controller.dto.ContextSearchForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.controller.dto.WordEntryForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Word;
import org.planetearth.words.service.ContextService;
import org.planetearth.words.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller of MVC Model.
 *
 * @author katsuyuki.t
 */
@Controller
@Getter
@Setter
@RequestMapping("/words/word")
public class WordController {

    @Autowired
    private WordService wordService;

    @Autowired
    private ContextService contextService;

    @ModelAttribute
    public WordEntryForm setUpEntryForm() {
        return new WordEntryForm();
    }

    @ModelAttribute
    public TextSearchForm setUpSearchForm() {
        return new TextSearchForm();
    }

    @GetMapping(value = "/entry")
    public ModelAndView entry() {
        ModelAndView mav = new ModelAndView("wordEntry");
        List<Context> contexts = contextService.findAll();
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/register")
    public String register(@Validated WordEntryForm wordEntry, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            List<Context> contexts = contextService.findAll();
            model.addAttribute("contexts", contexts);
            return "wordEntry";
        }
        Word stored = wordService
            .findByUniqueKey(wordEntry.getContextId(), wordEntry.getNotation());
        if (stored != null) { // already exists.
            List<Context> contexts = contextService.findAll();
            model.addAttribute("contexts", contexts);
            model.addAttribute("exists", Boolean.TRUE);
            return "wordEntry";
        }
        wordService.register(wordEntry);
        return "redirect:/words/word/entry";
    }

    @GetMapping(value = "/read/{id}")
    public ModelAndView read(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("word");
        Word word = wordService.findById(id);
        List<Context> contexts = contextService.findAll();
        mav.addObject("browsing", Boolean.TRUE);
        mav.addObject("word", word);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "edit")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("word");
        mav.addObject("editing", Boolean.TRUE);
        Word word = wordService.findById(id);
        mav.addObject("word", word);
        List<Context> contexts = contextService.findAll();
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "confirm")
    public ModelAndView confirm(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("word");
        Word word = wordService.findById(id);
        mav.addObject("word", word);
        List<Context> contexts = contextService.findAll();
        mav.addObject("contexts", contexts);
        if (!CollectionUtils.isEmpty(word.getTerms())) {
            mav.addObject("referred", Boolean.TRUE);
        } else {
            mav.addObject("confirmation", Boolean.TRUE);
        }
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "remove")
    public String remove(@PathVariable("id") Long id) {
        wordService.remove(id);
        return "redirect:/words/word/list";
    }

    @PostMapping(value = "/edit/{id}", params = "goback")
    public String goback(@PathVariable("id") Long id) {
        return "redirect:/words/word/read/" + id;
    }

    @PostMapping(value = "/update/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("word") Word word) {
        wordService.update(word);
        return "redirect:/words/word/read/" + id;
    }

    @PostMapping(value = "/update/{id}", params = "cancel")
    public String cancel(@PathVariable("id") Long id, @ModelAttribute("word") Word word) {
        return "redirect:/words/word/read/" + id;
    }

    @GetMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("wordList");
        List<Word> words = wordService.findAll();
        List<Context> contexts = contextService.findAll();
        mav.addObject("words", words);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/list")
    public ModelAndView list(@ModelAttribute("contextSearchForm") ContextSearchForm searchForm) {
        ModelAndView mav = new ModelAndView("wordList");
        List<Word> words = wordService.findSubordinate(searchForm.getContextId());
        List<Context> contexts = contextService.findAll();
        mav.addObject("words", words);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/search")
    public ModelAndView search(@ModelAttribute("textSearchForm") TextSearchForm searchForm) {
        ModelAndView mav = new ModelAndView("wordList");
        List<Word> words = wordService.findBySearchForm(searchForm);
        List<Context> contexts = contextService.findAll();
        mav.addObject("words", words);
        mav.addObject("contexts", contexts);
        return mav;
    }
}
