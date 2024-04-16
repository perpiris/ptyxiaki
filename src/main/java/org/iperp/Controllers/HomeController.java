package org.iperp.Controllers;

import org.iperp.Utilities.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index(@RequestParam(name = "invalidRole", required = false) final Boolean invalidRole, final Model model) {
        if (invalidRole == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, "You are not authorized to view this area.");
        }

        return "home/index";
    }

}
