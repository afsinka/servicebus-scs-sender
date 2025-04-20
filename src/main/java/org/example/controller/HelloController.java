package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private StreamBridge streamBridge;

    @Value("${demo.binding.name}")
    private String bindingName;

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable String name) {
        streamBridge.send(bindingName, "Hello from " + name);
    }

}