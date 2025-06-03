package org.amber.asparagus.deeplinkdemo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/web/bagibagi/{*path}")
    public String serveBagiBagiPage(HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        String uriPrefixRemoved = uri.replace("/web/bagibagi/deeplink", "");
        System.out.println("Uri that come to bagibagi page : " + uri);
        model.addAttribute("deeplinkUrl", "https://deeplinkdemo2.onrender.com/deeplinkfallback" + uriPrefixRemoved);
//        model.addAttribute("deeplinkUrl", "intent://deeplink/path#Intent;scheme=org.amber.asparagus.applink;package=org.amber.asparagus.applink;end");

        return "bagibagi";
    }
}
