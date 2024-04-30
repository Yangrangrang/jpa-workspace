package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;    // 데이터를 실어서 view에 넘길 수 있음.
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello");
        return "hello";
    }
}
