package org.planetearth.words.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller of MVC Model.
 *
 * @author katsuyuki.t
 */
@Controller
public class UnclassifiedController {

    @RequestMapping("/words")
    public String words() {
        return "words";
    }

    @RequestMapping("/")
    public String top() {
        return "forward:words";
    }

    @RequestMapping("/index")
    public String index(@RequestParam(defaultValue = "NOT INDEX PARAM") String msg, Model model) {
        return "forward:words";
    }

}
