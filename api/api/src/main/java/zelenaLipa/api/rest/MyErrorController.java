package zelenaLipa.api.rest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        Integer statusCode = Integer.valueOf(status.toString());

        if(status != null) {

            if(statusCode == HttpStatus.FORBIDDEN.value()) {

                ModelAndView mv = new ModelAndView("error/error.html");
                ConditionChecker.checkVariables(mv);
                setErrorCode(mv, statusCode);
                return mv;

            }

        }

        ModelAndView mv = new ModelAndView("error/error.html");
        ConditionChecker.checkVariables(mv);
        setErrorCode(mv, statusCode);
        return mv;

        //return new RedirectView("/"); //Vrati homepage ako nije ništa

    }

    private void setErrorCode(ModelAndView mv, int errorCode) {
        mv.addObject("errorCode", errorCode);
    }

}
