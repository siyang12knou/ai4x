package com.kailoslab.ai4x.app.beantest;

import com.kailoslab.ai4x.py4spring.controller.ResultMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class PythonTestBeanController {

    @Lazy
    @Autowired
    private PythonTestBean pythonTestBean;

    @GetMapping("/{iam}")
    public ResultMessageDto test(@PathVariable String iam){
        PythonTestArgs args = new PythonTestArgs(iam);
        return new ResultMessageDto(pythonTestBean.test(args));
    }
}
