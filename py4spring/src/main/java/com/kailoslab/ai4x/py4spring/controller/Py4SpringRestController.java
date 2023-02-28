package com.kailoslab.ai4x.py4spring.controller;

import com.kailoslab.ai4x.py4spring.Py4SpringService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${ai4x.py4spring.path:/}")
public class Py4SpringRestController {

    private final Py4SpringService py4SpringService;

    @RequestMapping(value="/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResultMessageDto dispatch(HttpServletRequest request, @RequestBody(required=false) String body){
        try {
            Object data = py4SpringService.getDispatcher().dispatch(request, body);
            return new ResultMessageDto(data);
        } catch (Throwable ex) {
            return new ResultMessageDto(false, ex.getMessage());
        }
    }
}
