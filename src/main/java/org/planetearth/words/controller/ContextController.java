package org.planetearth.words.controller;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.planetearth.words.controller.dto.ContextEntryForm;
import org.planetearth.words.controller.dto.TextSearchForm;
import org.planetearth.words.domain.Context;
import org.planetearth.words.service.ContextService;
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

/**
 * Controller of MVC Model.
 *
 * @author katsuyuki.t
 */
@Controller
@Getter
@Setter
@RequestMapping("/words/context")
public class ContextController {

    @Autowired
    private ContextService contextService;

    @ModelAttribute
    public ContextEntryForm setUpEntryForm() {
        return new ContextEntryForm();
    }

    @ModelAttribute
    public TextSearchForm setUpSearchForm() {
        return new TextSearchForm();
    }

    @GetMapping(value = "/entry")
    public ModelAndView entry() {
        ModelAndView mav = new ModelAndView();
        List<Context> contexts = contextService.findAll();
        mav.setViewName("contextEntry");
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/register")
    public String register(@Validated ContextEntryForm entryForm, BindingResult binding,
        Model model) {
        if (binding.hasErrors()) {
            List<Context> contexts = contextService.findAll(); // preset
            model.addAttribute("contexts", contexts);
            return "contextEntry";
        }
        Boolean exists = contextService
            .existsSameNameContext(entryForm.getParentName(), entryForm.getName());
        if (exists) { // already exists.
            List<Context> contexts = contextService.findAll();
            model.addAttribute("contexts", contexts);
            model.addAttribute("exists", Boolean.TRUE);
            return "contextEntry";
        }
        contextService.register(entryForm);
        return "redirect:/words/context/list";
    }

    @GetMapping(value = "/read/{id}")
    public ModelAndView read(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView();
        Context context = contextService.findById(id);
        mav.setViewName("context");
        mav.addObject("browsing", Boolean.TRUE);
        mav.addObject("context", context);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "edit")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("context");
        Context context = contextService.findById(id);
        mav.addObject("editing", Boolean.TRUE);
        mav.addObject("context", context);
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "confirm")
    public ModelAndView confirm(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("context");
        Context context = contextService.findById(id);
        mav.addObject("context", context);
        if (contextService.existsReferrer(id)) {
            mav.addObject("referred", Boolean.TRUE);
        } else {
            mav.addObject("confirmation", Boolean.TRUE);
        }
        return mav;
    }

    @PostMapping(value = "/edit/{id}", params = "remove")
    public String remove(@PathVariable("id") Long id) {
        contextService.remove(id);
        return "redirect:/words/context/list";
    }

    @PostMapping(value = "/edit/{id}", params = "goback")
    public String goback(@PathVariable("id") Long id) {
        return "redirect:/words/context/read/" + id;
    }

    @PostMapping(value = "/update/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("context") Context context) {
        contextService.update(context);
        return "redirect:/words/context/read/" + id;
    }

    @PostMapping(value = "/update/{id}", params = "cancel")
    public String cancel(@PathVariable("id") Long id, @ModelAttribute("context") Context context) {
        return "redirect:/words/context/read/" + id;
    }

    @GetMapping(value = "/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView();
        List<Context> contexts = contextService.findAll();
        mav.setViewName("contextList");
        mav.addObject("contexts", contexts);
        return mav;
    }

    @PostMapping(value = "/search")
    public ModelAndView search(@ModelAttribute("textSearchForm") TextSearchForm searchForm) {
        ModelAndView mav = new ModelAndView();
        List<Context> contexts = contextService.findBySearchForm(searchForm);
        mav.setViewName("contextList");
        mav.addObject("contexts", contexts);
        return mav;
    }
}
