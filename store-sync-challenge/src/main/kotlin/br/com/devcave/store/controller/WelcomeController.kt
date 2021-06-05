package br.com.devcave.store.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WelcomeController {
    @GetMapping
    fun welcome(): String = "redirect:swagger-ui.html"
}