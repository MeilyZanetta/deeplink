package org.amber.asparagus.deeplinkdemo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/")
public class DeeplinkController {
    // Serve the apple-app-site-association (the list of approved apps that can handle bca link). The path has to be kept as is (following apple convention)
    // It is the same with assetlinks.json
    @GetMapping("/.well-known/apple-app-site-association")
    public ResponseEntity<String> appleAssociationFile() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String body = """
                {
                    "applinks": {
                        "details": [
                            {
                                "appID": "H2ESFFMXSH.com.sqi.rnd",
                                "paths": [ "/deeplink/bcamobile/promo/*", "/deeplink/bcamobile/bagibagi/*", "/deeplinkfallback/bcamobile/promo/*", "/deeplinkfallback/bcamobile/bagibagi/*", "/deeplink2/bcamobile/promo/*", "/deeplink2/bcamobile/bagibagi/*"]
                            }
                        ]
                    }
                }""";

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    // Handle deeplink (adjust the path according to the url design)
    // This controller will only be called when the app is not yet installed in the phone, or if in app browser (such as facebook) is intercepting the link
    @GetMapping("/deeplink/{*path}")
    public ResponseEntity<Void> deeplink(HttpServletRequest request) {
        String uri = request.getRequestURI();
        System.out.println("DeeplinkController uri : " + uri);
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        System.out.println("User agent : " + userAgent);
        HttpHeaders headers = new HttpHeaders();

        // Take action based on user agent.
        if (userAgent.contains("android")
                && !userAgent.contains("fb_iab") && !userAgent.contains("huawei")) {
            // redirect to playstore
            headers.setLocation(URI.create("https://play.google.com/store/apps/details?id=com.bca"));
        }
        else if(userAgent.contains("iphone")
                && !userAgent.contains("fb_iab") && !userAgent.contains("fbios")
                && !userAgent.contains("instagram") && !userAgent.contains("line")
        ) {
            // redirect to apple app store
            headers.setLocation(URI.create("https://apps.apple.com/id/app/bca-mobile/id551587254"));
        }
        else if(userAgent.contains("huawei")) {
            // redirect to huawei app gallery
            headers.setLocation(URI.create("https://appgallery.cloud.huawei.com/ag/n/app/C101512797?locale=en_US"));
        }
        else {
            // Fallback website (if the link is accessed from PC, or from whatsapp chat server to get thumbnail)
            headers.setLocation(URI.create("https://deeplinkdemo.onrender.com/web/bagibagi" + uri));
        }
        // Optionally handle cases of facebook, instagram

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // This is to handle facebook case
    @GetMapping("/deeplinkfallback/{*path}")
    public ResponseEntity<Void> deeplinkFallback(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        System.out.println("Deeplink Fallback uri : " + uri);
        System.out.println("User agent : " + userAgent);
        HttpHeaders headers = new HttpHeaders();

        System.out.println("Redirecting /deeplink2");
        headers.setLocation(URI.create("https://deeplinkdemo2.onrender.com/deeplink2" + uri));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // the redirect:/ doesn't work because this is RestController duh!
    }

    // This is to handle facebook case
    @GetMapping("/deeplink2/{*path}")
    public ResponseEntity<Void> deeplink2(HttpServletRequest request) {
        String uri = request.getRequestURI();
        System.out.println("Deeplink2 uri : " + uri);
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        System.out.println("User agent : " + userAgent);
        HttpHeaders headers = new HttpHeaders();
        if (userAgent.contains("android") && !userAgent.contains("huawei")) { //whatsapp user agent is whatsapp, while telegram is telegrambot
            System.out.println("Redirecting to store -- but now hardcoded");
            headers.setLocation(URI.create("https://play.google.com/store/apps/details?id=com.bca"));
        }
        else if(userAgent.contains("iphone")) {
            System.out.println("Redirecting to appstore -- but now hardcoded");
            headers.setLocation(URI.create("https://apps.apple.com/id/app/bca-mobile/id551587254"));
        }
        else if(userAgent.contains("huawei")) {
            headers.setLocation(URI.create("https://appgallery.cloud.huawei.com/ag/n/app/C101512797?locale=en_US"));
        }
        else {
            headers.setLocation(URI.create("https://deeplinkdemo.onrender.com/web/bagibagi" + uri));
        }
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // the redirect:/ doesn't work because this is RestController duh!
    }
}
