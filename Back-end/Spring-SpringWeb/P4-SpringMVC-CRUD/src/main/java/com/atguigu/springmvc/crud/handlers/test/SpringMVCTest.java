package com.atguigu.springmvc.crud.handlers.test;

import com.atguigu.springmvc.crud.dao.EmployeeDao;
import com.atguigu.springmvc.crud.entities.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@Controller
public class SpringMVCTest {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    ///////////////////////////////////////////////////////////////////////////
    // 异常处理
    ///////////////////////////////////////////////////////////////////////////

    @RequestMapping("/testSimpleMappingExceptionResolver")
    public String testSimpleMappingExceptionResolver(@RequestParam("i") int i) {
        String[] vals = new String[10];
        System.out.println(vals[i]);
        return "success";
    }

    /*
    测试 DefaultHandlerExceptionResolver，DefaultHandlerExceptionResolver 用于对 Spring MVC 中特殊的异常进行处理，由 Spring 添加，这里触发了请求方法不支持异常。
    */
    @RequestMapping(value = "/testDefaultHandlerExceptionResolver", method = RequestMethod.POST)
    public String testDefaultHandlerExceptionResolver() {
        System.out.println("calling testDefaultHandlerExceptionResolver...");
        return "success";
    }

    @RequestMapping("/testResponseStatusExceptionResolver1")
    public String testResponseStatusExceptionResolver1(@RequestParam("i") int i) {
        System.out.println("calling testResponseStatusExceptionResolver1...");
        if (i == 13) {
            throw new UserNameNotMatchPasswordException();
        }
        return "success";
    }

    /*被ResponseStatus标注，定义返回的响应码和说明。*/
    @ResponseStatus(reason = "测试", value = HttpStatus.NOT_FOUND)
    @RequestMapping("/testResponseStatusExceptionResolver2")
    public String testResponseStatusExceptionResolver2() {
        System.out.println("calling testResponseStatusExceptionResolver2...");
        return "success";
    }

    /*
     * 1. 在 @ExceptionHandler 方法的入参中可以加入 Exception 类型的参数， 该参数即对应发生的异常对象。
     * 2. @ExceptionHandler 方法的入参中不能传入 Map. 若希望把异常信息传导页面上，需要使用 ModelAndView 作为返回值。
     * 3. @ExceptionHandler 方法标记的异常有优先级的问题，找匹配度高的。
     * 4. @ControllerAdvice: 如果在当前 Handler 中找不到 @ExceptionHandler 方法来出来当前方法出现的异常，则将去 @ControllerAdvice 标记的类中查找 @ExceptionHandler 标记的方法来处理异常。
     */

    /*注销下面两个方法，则找 ControllerAdvice 的异常处理器。*/

    @ExceptionHandler({ArithmeticException.class})
    public ModelAndView handleArithmeticException(Exception ex) {
        System.out.println("出异常了: " + ex);
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("exception", ex);
        return mv;
    }

    @ExceptionHandler({RuntimeException.class})
    public ModelAndView handleArithmeticException2(Exception ex) {
        System.out.println("[出异常了]: " + ex);
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("exception", ex);
        return mv;
    }

    @RequestMapping("/testExceptionHandlerExceptionResolver")
    public String testExceptionHandlerExceptionResolver(@RequestParam("i") int i) {
        System.out.println("result: " + (10 / i));
        return "success";
    }

    ///////////////////////////////////////////////////////////////////////////
    // 文件上传
    ///////////////////////////////////////////////////////////////////////////

    @RequestMapping("/testFileUpload")
    public String testFileUpload(@RequestParam("desc") String desc, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("desc: " + desc);
        System.out.println("OriginalFilename: " + file.getOriginalFilename());
        System.out.println("InputStream: " + file.getInputStream());
        return "success";
    }

    ///////////////////////////////////////////////////////////////////////////
    // 国际化
    ///////////////////////////////////////////////////////////////////////////

    @RequestMapping("/i18n")
    public String testI18n(Locale locale) {
        //获取国际化资源
        String val = messageSource.getMessage("i18n.user", null, locale);
        System.out.println(val);
        return "i18n";
    }

    ///////////////////////////////////////////////////////////////////////////
    // HttpMessageConverter
    ///////////////////////////////////////////////////////////////////////////

    /*测试文件下载*/
    @RequestMapping("/testResponseEntity")
    public ResponseEntity<byte[]> testResponseEntity(HttpSession session) throws IOException {
        //读取内容
        ServletContext servletContext = session.getServletContext();
        InputStream in = servletContext.getResourceAsStream("/files/abc.txt"/*路径相对于 webapp*/);
        byte[] body = new byte[in.available()];
        long len = in.read(body);

        //构建响应信息
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=abc.txt");
        HttpStatus statusCode = HttpStatus.OK;

        return new ResponseEntity<>(body, headers, statusCode);
    }

    /*测试将流转变为 String*/
    @ResponseBody
    @RequestMapping("/testHttpMessageConverter")
    public String testHttpMessageConverter(@RequestBody String body) {
        System.out.println(body);
        return "helloworld! " + new Date();
    }

    @ResponseBody
    @RequestMapping("/testJson")
    public Collection<Employee> testJson() {
        return employeeDao.getAll();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Conversion Service
    ///////////////////////////////////////////////////////////////////////////

    @RequestMapping("/testConversionServiceConverter")
    public String testConverter(@RequestParam("employee") Employee employee) {
        System.out.println("save: " + employee);
        employeeDao.save(employee);
        return "redirect:/emps";
    }

}
