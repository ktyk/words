package org.planetearth.words.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ExceptionHandler.
 * 
 * @author katsuyuki.t
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = FileNotFoundException.class)
    public void handle(FileNotFoundException ex, HttpServletResponse response, Model model) throws IOException {
        System.out.println("handling file not found exception");
        // ex.printStackTrace();
        response.sendError(404, ex.getMessage());
        model.addAttribute("status", response.getStatus());
    }

    @ExceptionHandler(value = IOException.class)
    public void handle(IOException ex, HttpServletResponse response, Model model) throws IOException {
        System.out.println("handling io exception");
        // ex.printStackTrace();
        response.sendError(500, ex.getMessage());
        model.addAttribute("status", response.getStatus());
    }
}
