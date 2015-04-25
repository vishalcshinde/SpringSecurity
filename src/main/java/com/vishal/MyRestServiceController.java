package com.vishal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class MyRestServiceController {
   
    @Autowired
    DemoService demoService;
    
    @RequestMapping(value = "MyService", method = RequestMethod.GET)
    public @ResponseBody String SayHello() {
        return "Hello Wrold " + demoService.getData();
    }
}
