package org.planetearth.words.controller;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.planetearth.words.controller.dto.TermEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.domain.Term;
import org.planetearth.words.service.ContextService;
import org.planetearth.words.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Sets;

/**
 * Controller of MVC Model.
 *
 * @author katsuyuki.t
 */
@Controller
@Getter
@Setter
@RequestMapping("/words/term")
public class TermController {

    @Autowired
    private TermService termService;

    @Autowired
    private ContextService contextService;

    @ModelAttribute
    public TermEntryForm setUpEntryForm() {
        return new TermEntryForm();
    }

    @ModelAttribute
    public TextSearchForm setUpSearchForm() {
        return new TextSearchForm();
    }

    @GetMapping(value = "/entry")
    public ModelAndView entry() {
        ModelAndView mav = new ModelAndView("termEntry");
        List<Context> contexts = contextService.findAll();
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/register")
    public String register(@Validated TermEntryForm termEntry, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            List<Context> contexts = contextService.findAll();
            model.addAttribute("contexts", contexts);
            return "termEntry";
        }
        Term stored = termService.findByUniqueKey(termEntry.getContextId(), termEntry.getName());
        if (stored != null) { // already exists.
            List<Context> contexts = contextService.findAll();
            model.addAttribute("contexts", contexts);
            model.addAttribute("exists", Boolean.TRUE);
            return "termEntry";
        }
        termService.register(termEntry);
        return "redirect:/words/term/list";
    }

    @PostMapping(value = "/register", params = "translate")
    public String translate(TermEntryForm termEntry, Model model) {
        Set<Long> wordIds = Sets.newHashSet();
        String translated = termService
            .translate(termEntry.getContextId(), termEntry.getName(), wordIds);
        termEntry.setTranslation(translated);
        termEntry.setWordIds(wordIds);
        List<Context> contexts = contextService.findAll();
        model.addAttribute("contexts", contexts);
        return "termEntry";
    }

    @PostMapping(value = "/update/{id}", params = "translate")
    public ModelAndView translate(@PathVariable("id") Long id, @ModelAttribute("term") Term term) {
        Set<Long> wordIds = Sets.newHashSet();
        String translated = termService.translate(term.getContextId(), term.getName(), wordIds);
        term.setTranslation(translated);
        term.setWordIds(wordIds);
        List<Context> contexts = contextService.findAll();
        ModelAndView mav = new ModelAndView("term");
        mav.addObject("editing", Boolean.TRUE);
        mav.addObject("term", term);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @GetMapping(value = "/read/{id}")
    public ModelAndView read(@PathVariable("id") Long id) {
        Term term = termService.findById(id);
        term.flattenParagraphs();
        List<Context> contexts = contextService.findAll();
        ModelAndView mav = new ModelAndView("term");
        mav.addObject("browsing", Boolean.TRUE);
        mav.addObject("term", term);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "edit")
    public ModelAndView edit(@PathVariable("id") Long id) {
        Term term = termService.findById(id);
        term.flattenParagraphs();
        List<Context> contexts = contextService.findAll();
        ModelAndView mav = new ModelAndView("term");
        mav.addObject("editing", Boolean.TRUE);
        mav.addObject("term", term);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "confirm")
    public ModelAndView confirm(@PathVariable("id") Long id) {
        Term term = termService.findById(id);
        term.flattenParagraphs();
        List<Context> contexts = contextService.findAll();
        ModelAndView mav = new ModelAndView("term");
        mav.addObject("confirmation", Boolean.TRUE);
        mav.addObject("term", term);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "remove")
    public String remove(@PathVariable("id") Long id) {
        termService.remove(id);
        return "redirect:/words/term/list";
    }

    @PostMapping(value = "/edit/{id}", params = "goback")
    public String goback(@PathVariable("id") Long id) {
        return "redirect:/words/term/read/" + id;
    }

    @PostMapping(value = "/update/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("term") Term term) {
        termService.update(term);
        return "redirect:/words/term/read/" + id;
    }

    @PostMapping(value = "/update/{id}", params = "cancel")
    public String cancel(@PathVariable("id") Long id, @ModelAttribute("term") Term term) {
        return "redirect:/words/term/read/" + id;
    }

    @GetMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("termList");
        List<Term> terms = termService.findAll();
        List<Context> contexts = contextService.findAll();
        mav.addObject("terms", terms);
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/search")
    public ModelAndView search(@ModelAttribute("textSearchForm") TextSearchForm searchForm) {
        ModelAndView mav = new ModelAndView("termList");
        List<Term> terms = termService.findBySearchForm(searchForm);
        List<Context> contexts = contextService.findAll();
        mav.addObject("terms", terms);
        mav.addObject("contexts", contexts);
        return mav;
    }
}
