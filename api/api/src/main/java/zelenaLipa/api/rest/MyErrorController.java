package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.service.ModelAndViewBuilderService;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

    @Autowired
    ModelAndViewBuilderService modelAndViewBuilderService;

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = "Unfortunately, there has been an error :( ";

        Integer statusCode = Integer.valueOf(status.toString());

        ModelAndView mv = new ModelAndView("error/error.html");
        modelAndViewBuilderService.fillNavigation(mv);

        if(message == null) message = "";
        mv.addObject("message", message);
        mv.addObject("errorCode", statusCode);
        return mv;

    }

}