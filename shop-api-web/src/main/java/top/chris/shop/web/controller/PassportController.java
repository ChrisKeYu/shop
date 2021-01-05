package top.chris.shop.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.pojo.vo.UsersVo;
import top.chris.shop.util.CookieUtils;
import top.chris.shop.util.JsonResult;
import top.chris.shop.pojo.bo.UsersBo;
import top.chris.shop.service.PassportService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 1、暂时使用纯Cookie的方式完成用户登录信息的保存和共享。
 * cookie几大重要属性：
 *      path:表示 cookie 影响到的路径，如果路径不能匹配时，浏览器在访问对应路径资源的服务器时，不携带Cookie发送。
 *           例如:path=www.baidu.com/a/1,www.baidu.com/a/2,那么访问对于服务器时携带cookie，而访问www.baidu.com/b/1就不携带cookie。
 *               path=/ 表示domain域名下的所有资源，访问时都会携带cookie
 *
 *      domain:域名，默认是当前域名，只要是该域名下的请求，都会让浏览器携带cookie去访问。例如domain=www.baidu.com ->wenku.baidu.com和music.baidu.com，
 *             访问时都可以携带对应的cookie去访问。使得cookie中保存的用户后台数据实现了域共享，不同域拿到了cookie的信息就可对向自身后台服务器发送认证请求，
 *             实现用户在不同应用保持登录状态。这就是为什么在百度域名登录自己账号后，在旗下域名例如wenku.baidu.com也能够同步保持我在百度登录的状态的原因。
 *             在本例中:由于我们的前端的域名不是标准的www.xx.com而是127.0.0.1,那么在CookieUtils类的doSetCookie方法中domainName变量就会被设置为默认的
 *             localhost作为domain域名来使用。所以，后台服务器返回给浏览器保存的cookie对象中的domain属性值是localhost。而我们前端项目的浏览器域名(domain)使
 *             用的不是localhost，所以前端项目根据domain在浏览器中发送请求到前端服务器时，不会携带cookie到前端服务器，那么前端服务器就没办法获取到数据，没数据
 *             就没办法进行页面数据绑定。此时我们需要将前端项目的域名进行修改，修改为localhost后，就可以在浏览器发送请求到前端服务器，同时也会携带浏览器中的cookie
 *             到前端服务器中，这样前端服务器就可获取到后台服务器保存在浏览器中cookie对象，有了这个对象就可实现页面与数据的绑定。
 *      maxage:最大失效时间(毫秒),设置在多少后失效
 *      key：
 *      value：
 */

@Api("登录和注册控制器")
@RestController
@RequestMapping("/passport")
public class  PassportController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private PassportService passportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShopProperties shopProperties;

    @ApiOperation("查看注册用户是否已经存在")
    @GetMapping("/usernameIsExist")
    public JsonResult usernameIsExist(String username){
        return passportService.usernameIsExist(username.trim()); //传入去除掉首尾空格的后字符串
    }

    @ApiOperation("用户注册接口")
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("/regist")
    public JsonResult regist(@Valid @RequestBody UsersBo usersBo) throws JsonProcessingException { //@Valid目的是调用UsersBo对象对象中的验证规则去验证前端传入参数的合法性,第二个注解表示接受的参数必须是Json格式，参数存放在payLoad中
        if (usersBo.getConfirmPassword() == null){
            return JsonResult.isErr(500,"请再次输入密码");
        }
        usersBo.setUsername(usersBo.getUsername().trim());
        usersBo.setPassword(usersBo.getPassword().trim());
        usersBo.setConfirmPassword(usersBo.getConfirmPassword().trim());
        if (! StringUtils.equals(usersBo.getPassword(),usersBo.getConfirmPassword())){
            return JsonResult.isErr(500,"两次输入密码不一致，请重新输入");
        }
        UsersVo userVo = passportService.regist(usersBo);
        //将UserVo对象写入Cookie中
        CookieUtils.setCookie(request,response,"user",objectMapper.writeValueAsString(userVo),604800,true);
        return JsonResult.isOk(userVo); //passportService.regist(usersBo)
    }

    //TODO:登录存在漏洞
    @ApiOperation("用登录接口")
    @Transactional(propagation = Propagation.REQUIRED)
    @PostMapping("/login")
    public JsonResult login(@Valid @RequestBody UsersBo usersBo,HttpServletRequest request ,HttpServletResponse httpServletResponse) throws JsonProcessingException {
        usersBo.setUsername(usersBo.getUsername().trim());
        usersBo.setPassword(usersBo.getPassword().trim());
        UsersVo usersVo = passportService.login(usersBo);

        if (usersVo == null){
            return JsonResult.isErr(500,"账号或密码输入错误,请重新输入。");
        }
        //使用Session保存用户登录状态
        HttpSession session = request.getSession();
        session.setAttribute("user",usersVo);
        CookieUtils.setCookie(request,response,"user",objectMapper.writeValueAsString(usersVo),604800,true);

        return JsonResult.isOk(usersVo);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation("用户退出接口")
    @PostMapping("/logout")
    public JsonResult logout(){
        //获取Session,false代表：不创建session对象，只是从request中获取。
        HttpSession session = request.getSession(false);
        if (session != null){
            //移除session数据
            session.removeAttribute("user");
        }
        //只是抹去了浏览器中的对应key的cookie。单纯删除cookie，在单体架构中使用。
        CookieUtils.deleteCookie(request,response,"user");
        //只是抹去了浏览器中的对应key的cookie。单纯删除cookie，在单体架构中使用。
        CookieUtils.deleteCookie(request,response,shopProperties.getShopCarCookieName());
        return JsonResult.ok();
    }
}
