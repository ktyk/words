package org.planetearth.words.controller;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.planetearth.words.controller.dto.ParagraphEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Paragraph;
import org.planetearth.words.service.ParagraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/words/paragraph")
public class ParagraphController {

    @Autowired
    private ParagraphService paragraphService;

    @ModelAttribute
    public ParagraphEntryForm setUpEntryForm() {
        return new ParagraphEntryForm();
    }

    @ModelAttribute
    public TextSearchForm setUpSearchForm() {
        return new TextSearchForm();
    }

    @GetMapping(value = "/entry")
    public String entry() {
        return "paragraphEntry";
    }

    @PostMapping(value = "/register")
    public String register(ParagraphEntryForm paragraphEntry, Model model) {
        paragraphService.register(paragraphEntry);
        return "redirect:/words/paragraph/list";
    }

    @GetMapping(value = "/read/{id}")
    public ModelAndView read(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView();
        Paragraph paragraph = paragraphService.findById(id);
        mav.setViewName("paragraph");
        mav.addObject("glossary", paragraph.fetchGlossary());
        mav.addObject("browsing", Boolean.TRUE);
        mav.addObject("paragraph", paragraph);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "edit")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("paragraph");
        Paragraph paragraph = paragraphService.findById(id);
        mav.addObject("editing", Boolean.TRUE);
        mav.addObject("paragraph", paragraph);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "confirm")
    public ModelAndView confirm(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("paragraph");
        Paragraph paragraph = paragraphService.findById(id);
        mav.addObject("confirmation", Boolean.TRUE);
        mav.addObject("paragraph", paragraph);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "remove")
    public String remove(@PathVariable("id") Long id) {
        paragraphService.remove(id);
        return "redirect:/words/paragraph/list";
    }

    @PostMapping(value = "/edit/{id}", params = "goback")
    public String goback(@PathVariable("id") Long id) {
        return "redirect:/words/paragraph/read/" + id;
    }

    @PostMapping(value = "/update/{id}")
    public String update(@PathVariable("id") Long id,
        @ModelAttribute("paragraph") Paragraph paragraph) {
        paragraphService.update(paragraph);
        return "redirect:/words/paragraph/read/" + id;
    }

    @PostMapping(value = "/update/{id}", params = "cancel")
    public String cancel(@PathVariable("id") Long id,
        @ModelAttribute("paragraph") Paragraph paragraph) {
        return "redirect:/words/paragraph/read/" + id;
    }

    @GetMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView();
        List<Paragraph> paragraphs = paragraphService.findAll();
        mav.setViewName("paragraphList");
        mav.addObject("paragraphs", paragraphs);
        return mav;
    }

    @PostMapping(value = "/search")
    public ModelAndView search(@ModelAttribute("textSearchForm") TextSearchForm searchForm) {
        ModelAndView mav = new ModelAndView();
        List<Paragraph> paragraphs = paragraphService.findBySearchForm(searchForm);
        mav.setViewName("paragraphList");
        mav.addObject("paragraphs", paragraphs);
        return mav;
    }
}
