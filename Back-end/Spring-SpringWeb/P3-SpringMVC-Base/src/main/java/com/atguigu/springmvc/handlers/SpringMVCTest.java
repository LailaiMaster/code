package com.atguigu.springmvc.handlers;

import com.atguigu.springmvc.entities.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//SessionAttributes的作用，对应下面 testSessionAttributes 方法，将名为 user 的属性以及类型为 String 的属性放入 Session 中。
@SessionAttributes(value = {"user"}, types = {String.class})
@RequestMapping("/springmvc")
@Controller
public class SpringMVCTest {

    private static final String SUCCESS = "success";

    public SpringMVCTest() {
        System.out.println("Charset.defaultCharset().name(): " + Charset.defaultCharset().name());
    }

    ///////////////////////////////////////////////////////////////////////////
    // 返回视图
    ///////////////////////////////////////////////////////////////////////////

    @RequestMapping("/testRedirect")
    public String testRedirect() {
        System.out.println("testRedirect");
        return "redirect:/index.jsp";
    }

    @RequestMapping("/testView")
    public String testView() {
        System.out.println("testView");
        return "helloView";
    }

    @RequestMapping("/testViewAndViewResolver")
    public String testViewAndViewResolver() {
        System.out.println("testViewAndViewResolver");
        return SUCCESS;
    }

    ///////////////////////////////////////////////////////////////////////////
    // SessionAttributes
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @SessionAttributes 除了可以通过属性名指定需要放到会话中的属性外(实际上使用的是 value 属性值)。
     * 还可以通过模型属性的对象类型指定哪些模型属性需要放到会话中(实际上使用的是 types 属性值)。
     *
     * 注意：该注解只能放在类的上面，而不能修饰放方法。
     */
    @RequestMapping("/testSessionAttributes")
    public String testSessionAttributes(Map<String, Object> map) {
        User user = new User("Tom", "123456", "tom@atguigu.com", 15);
        map.put("user", user);
        map.put("school", "atguigu");
        return SUCCESS;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ModelAttribute
    ///////////////////////////////////////////////////////////////////////////

    /*
     * 1. 有 @ModelAttribute 标记的方法，会在每个目标方法执行之前被 SpringMVC 调用。
     * 2. @ModelAttribute 注解也可以来修饰目标方法 POJO 类型的入参，其 value 属性值有如下的作用:
     * 	1). SpringMVC 会使用 value 属性值在 implicit Model 中查找对应的对象，若存在则会直接传入到目标方法的入参中。
     * 	2). SpringMVC 会以 value  属性值为 key，将 POJO 类型的对象为 value，存入到 request 中。
     * 	3). ModelAttribute 设置了 @RequestParam(value = "id", required = true)，这时，要求请求的参数中有 id 才会执行此方法。
     *
     * 说明：
     *          implicit Model 即下面方法的 map 参数，implicitModel 可以是 Map 或 Model。
     *          Spring MVC 在调用方法前会创建一个隐含的模型对象作为模型数据的存储容器。
     *          如果方法的入参为 Map 或 Model 类型，Spring MVC 会将隐含模型的引用传递给这些入参。
     */
    @ModelAttribute
    public void getUser(@RequestParam(value = "id", required = false) Integer id, Map<String, Object> map) {
        System.out.println("modelAttribute method: getUser");
        if (id != null) {
            //模拟从数据库中获取对象
            User user = new User(1, "Tom", "123456", "tom@atguigu.com", 12);
            System.out.println("从数据库中获取一个对象: " + user);
            //这里的 key 为  "user" 是有规范的，其对应下面目标方法 testModelAttribute，入参类型 User 的类名，只是将第一字母改为小写。
            //比如 User->user，BeautifulGirl->beautifulGirl
            map.put("user", user);
        }
    }

    @ModelAttribute
    public void getNameA(Map<String, Object> map) {
        System.out.println("modelAttribute method: getNameA");
        map.put("abc", "abc");
    }

    @ModelAttribute
    public void getNameB(Map<String, Object> map) {
        System.out.println("modelAttribute method: getNameB");
        map.put("def", "def");
    }

    /*
     * 运行流程:
     *
     *      1. 执行 @ModelAttribute 注解修饰的方法，从数据库中取出对象，把对象放入到了 Map 中，键为 user。
     *      2. SpringMVC 从 Map 中取出 User 对象，并把表单的请求参数赋给该 User 对象的对应属性。
     *      3. SpringMVC 把上述对象传入目标方法的参数。
     *
     * 注意：在 @ModelAttribute 修饰的方法中，放入到 Map 时的键需要和目标方法入参类型类名字符串（第一个字母小写的）一致。
     *
     * SpringMVC 确定目标方法 POJO 类型入参的过程
     *
     *          1. 确定一个 key：
     *               1). 若目标方法的 POJO 类型的参数木有使用 @ModelAttribute 作为修饰，则 key 为 POJO 类名第一个字母的小写。
     *               2). 若使用了  @ModelAttribute 的 value 属性来修饰，则 key 为 @ModelAttribute 注解的 value 属性值。
     *
     *          2. 在 implicitModel 中查找 key 对应的对象，若存在，则作为入参传入。
     *                   1). 若在 @ModelAttribute 标记的方法中，往 Map 中保存过，且 key 和 1 确定的 key 一致，则会获取到。
     *
     *          3. 若 implicitModel 中不存在 key 对应的对象，则检查当前的 Handler 是否使用 @SessionAttributes 注解修饰。
     *              若使用了该注解，且 @SessionAttributes 注解的 value 属性值中包含了 key，则会从 HttpSession 中来获取 key 所
     *              对应的 value 值，若存在则直接传入到目标方法的入参中.，若不存在则将抛出异常。
     *
     *          4. 若 Handler 没有标识 @SessionAttributes 注解或 @SessionAttributes 注解的 value 值中不包含 key，则会通过反射来创建 POJO 类型的参数，传入为目标方法的参数。
     *
     *          5. SpringMVC 会把 key 和 POJO 类型的对象保存到 implicitModel 中，进而会保存到 request 中.
     *
     * 源代码分析的流程
     *
     * 1. 调用 @ModelAttribute 注解修饰的方法. 实际上把 @ModelAttribute 方法中 Map 中的数据放在了 implicitModel 中.
     * 2. 解析请求处理器的目标参数，实际上该目标参数来自于 WebDataBinder 对象的 target 属性
     *          1). 创建 WebDataBinder 对象：
     *                  ①. 确定 objectName 属性: 若传入的 attrName 属性值为 ""，则 objectName 为类名第一个字母小写.（com.atguigu.springmvc.entities.User --> user）
     *                      注意: attrName. 若目标方法的 POJO 属性使用了 @ModelAttribute 来修饰，比如（testModelAttribute(@ModelAttribute("user")  User user)），则 attrName 值即为 @ModelAttribute 的 value 属性值。
     *
     *                  ②. 确定 target 属性：
     *                  > 在 implicitModel 中查找 attrName 对应的属性值，若存在则 ok。
     *                  > 若不存在：则验证当前 Handler 是否使用了 @SessionAttributes 进行修饰，若使用了，则尝试从 Session 中获取 attrName 所对应的属性值，若 session 中没有对应的属性值，则抛出了异常。
     *                  > 若 Handler 没有使用 @SessionAttributes 进行修饰，或 @SessionAttributes 中没有使用 value 值指定的 key 和 attrName 相匹配，则通过反射创建了 POJO 对象。
     *
     *          2). SpringMVC 把表单的请求参数赋给了 org.springframework.web.bind.WebDataBinder 的 target 对应的属性。
     *          3). SpringMVC 会把 WebDataBinder 的 attrName 和 target 给到 implicitModel. 进而传到 request 域对象中。
     *          4). 把 WebDataBinder 的 target 作为参数传递给目标方法的入参。
     */
    @RequestMapping("/testModelAttribute")
    public String testModelAttribute(/*@ModelAttribute("user") 可以使用这种方式显示指定 key 名*/ User user) {
        //user 对象由 Spring MVC 调用 getUser 方法，然后取出来传给 testModelAttribute。
        System.out.println("testModelAttribute: use = " + user);
        return SUCCESS;
    }

    @RequestMapping("/testModelAttributeUsingMap")
    public String testModelAttributeUsingMap(Map<String, Object> map) {
        System.out.println("testModelAttributeUsingMap: map = " + map);
        return SUCCESS;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Map 和 ModelMap、ModelAndView
    ///////////////////////////////////////////////////////////////////////////

    /*
     * 目标方法可以添加 Map 类型(实际上也可以是 Model 类型或 ModelMap 类型)的参数。
     */
    @RequestMapping("/testMapWithParams")
    public String testMapWithParams(Map<String, Object> map, @RequestParam("a") String name) {
        System.out.println("testMapWithParams: map = " + map);
        System.out.println("testMapWithParams: a= " + name);
        //默认放入了 requestScope 中
        map.put("names", Arrays.asList("Jordan", "Li", "Jack"));
        return SUCCESS;
    }

    /*
     * 目标方法可以添加 Map 类型(实际上也可以是 Model 类型或 ModelMap 类型)的参数。
     */
    @RequestMapping("/testMap")
    public String testMap(Map<String, Object> map) {
        System.out.println("testMap: " + map);
        //默认放入了 requestScope 中
        map.put("names", Arrays.asList("Tom", "Jerry", "Mike"));
        return SUCCESS;
    }

    /*
     * 目标方法的返回值可以是 ModelAndView 类型。
     * 其中可以包含视图和模型信息
     * SpringMVC 会把 ModelAndView 的 model 中数据放入到 request 域对象中。
     */
    @RequestMapping("/testModelAndView")
    public ModelAndView testModelAndView() {
        ModelAndView modelAndView = new ModelAndView(SUCCESS);
        //添加模型数据到 ModelAndView 中.
        modelAndView.addObject("time", new Date());
        return modelAndView;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 入参
    ///////////////////////////////////////////////////////////////////////////

    /*
     * 可以使用 Serlvet 原生的 API 作为目标方法的参数 具体支持以下类型
     *
     * 			HttpServletRequest
     * 			HttpServletResponse
     * 			HttpSession
     * 			java.security.Principal
     * 			Locale InputStream
     * 			OutputStream
     * 			Reader
     * 			Writer
     */
    @RequestMapping("/testServletAPI")
    public void testServletAPI(HttpServletRequest request, HttpServletResponse response, Writer out) throws IOException {
        System.out.println("testServletAPI, " + request + ", " + response);
        out.write("hello springmvc");
    }

    /*
     * Spring MVC 会按请求参数名和 POJO 属性名进行自动匹配， 自动为该对象填充属性值。支持级联属性。如：dept.deptId、dept.address.tel 等
     */
    @RequestMapping("/testPojo")
    public String testPojo(User user) {
        System.out.println("testPojo: " + user);
        return SUCCESS;
    }

    /*
     * 了解：CookieValue: 映射一个 Cookie 值. 属性同 @RequestParam
     */
    @RequestMapping("/testCookieValue")
    public String testCookieValue(@CookieValue("JSESSIONID") String sessionId) {
        System.out.println("testCookieValue: sessionId: " + sessionId);
        return SUCCESS;
    }

    /*
     * 了解: 映射请求头信息 用法同 @RequestParam
     */
    @RequestMapping("/testRequestHeader")
    public String testRequestHeader(@RequestHeader(value = "Accept-Language") String al) {
        System.out.println("testRequestHeader, Accept-Language: " + al);
        return SUCCESS;
    }

    /*
     * @RequestParam 来映射请求参数. value 值即请求参数的参数名 required 该参数是否必须，默认为 true
     *
     * 对于基本数据类型，要么给用 defaultValue 设置默认值，要么 required 为 true，否则 spring 不知道到底传什么值。（此时可以考虑使用包装类型）
     */
    @RequestMapping(value = "/testRequestParam")
    public String testRequestParam(
            @RequestParam(value = "username") String un,
            @RequestParam(value = "age", required = false, defaultValue = "0") int age
    ) {
        System.out.println("testRequestParam, username: " + un + ", age: " + age);
        return SUCCESS;
    }

    ///////////////////////////////////////////////////////////////////////////
    // URL 映射
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Rest 风格的 URL. 以 CRUD 为例:
     * 		新增: /order POST
     * 		修改: /order/1/  PUT update?id=1
     * 		获取: /order/1 GET get?id=1
     * 		删除: /order/1 DELETE delete?id=1
     *
     * 如何发送 PUT 请求和 DELETE 请求呢 ?
     * 		1. 需要配置 HiddenHttpMethodFilter
     * 		2. 需要发送 POST 请求
     * 		3. 需要在发送 POST 请求时携带一个 name="_method" 的隐藏域, 值为 DELETE 或 PUT
     *
     * 在 SpringMVC 的目标方法中如何得到 id 呢? 使用 @PathVariable 注解
     */
    @RequestMapping(value = "/testRest/{id}", method = RequestMethod.PUT)
    public String testRestPut(@PathVariable Integer id) {
        System.out.println("testRest Put: " + id);
        return SUCCESS;
    }

    @RequestMapping(value = "/testRest/{id}", method = RequestMethod.DELETE)
    public String testRestDelete(@PathVariable Integer id) {
        System.out.println("testRest Delete: " + id);
        return SUCCESS;
    }

    @RequestMapping(value = "/testRest", method = RequestMethod.POST)
    public String testRest() {
        System.out.println("testRest POST");
        return SUCCESS;
    }

    @RequestMapping(value = "/testRest/{id}", method = RequestMethod.GET)
    public String testRest(@PathVariable Integer id) {
        System.out.println("testRest GET: " + id);
        return SUCCESS;
    }

    /*
     * @PathVariable 可以来映射 URL 中的占位符到目标方法的参数中.
     */
    @RequestMapping("/testPathVariable/{id}")
    public String testPathVariable(@PathVariable("id") Integer id) {
        System.out.println("testPathVariable: " + id);
        return SUCCESS;
    }

    @RequestMapping("/testAntPath/*/abc")
    public String testAntPath() {
        System.out.println("testAntPath");
        return SUCCESS;
    }

    /*
     * 了解: 可以使用 params 和 headers 来更加精确的映射请求. params 和 headers 支持简单的表达式.
     */
    @RequestMapping(value = "testParamsAndHeaders", params = {"username", "age!=10"}, headers = {"Accept-Language=en-US,zh;q=0.8"})
    public String testParamsAndHeaders() {
        System.out.println("testParamsAndHeaders");
        return SUCCESS;
    }

    /*
     * 常用: 使用 method 属性来指定请求方式
     */
    @RequestMapping(value = "/testMethod", method = RequestMethod.POST)
    public String testMethod() {
        System.out.println("testMethod");
        return SUCCESS;
    }

    /*
     * @RequestMapping 除了修饰方法, 还可来修饰类 。
     * 		类定义处: 提供初步的请求映射信息。相对于 WEB 应用的根目录
     * 		方法处: 提供进一步的细分映射信息。 相对于类定义处的 URL。若类定义处未标注 @RequestMapping，则方法处标记的 URL 相对于 WEB 应用的根目录
     */
    @RequestMapping("/testRequestMapping")
    public String testRequestMapping() {
        System.out.println("testRequestMapping");
        return SUCCESS;
    }

}
