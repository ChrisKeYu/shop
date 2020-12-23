package top.chris.shop.web.controller;

import com.alipay.api.domain.UserVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chris.shop.config.SFtpConfig;
import top.chris.shop.exception.PicException;
import top.chris.shop.pojo.bo.UserInfoBo;
import top.chris.shop.pojo.vo.UsersVo;
import top.chris.shop.service.MyCenterService;
import top.chris.shop.service.PassportService;
import top.chris.shop.util.CookieUtils;
import top.chris.shop.util.FileNameUtils;
import top.chris.shop.util.JsonResult;
import top.chris.shop.util.SFtpUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log
@Api("个人中心的控制器，管理个人信息")
@RestController
@RequestMapping("/userInfo")
public class MyCenterController {
    @Autowired
    private MyCenterService myCenterService;

    @Autowired
    private SFtpConfig sFtpConfig;

    @Autowired
    private PassportService passportService;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation("根据用户id查询指定用户个人信息")
    @PostMapping("/renderUserInfo")
    public JsonResult getUserInfo(String userId){
        return JsonResult.isOk(myCenterService.queryUserInfoByUserId(userId));
    }

    @ApiOperation("根据用户id修改用户个人信息")
    @PostMapping("/update")
    public JsonResult updateUserInfo(@RequestBody UserInfoBo bo, HttpServletRequest request, HttpServletResponse response){
        UsersVo user = (UsersVo) request.getSession().getAttribute("user");
        log.info("前端传入的最新用户信息:"+ ReflectionToStringBuilder.toString(bo));
        return JsonResult.isOk( myCenterService.updateUserInfoByUserId(bo,user.getId()));
    }

    @ApiOperation("根据userId修改头像")
    @PostMapping("/uploadFace")
    public JsonResult uploadFace(String userId, @RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response) throws IOException {
        //从user表中获取用户的信息
        UsersVo usersVo = passportService.renderUser(userId);
        //原来存储上传结果
        String result = " ";
        //判断上传的照片是否为空
        if (file.isEmpty()) {
            throw new PicException("上传的照片为空");
        }
        try {
            //1.获取图片原来的名字
            String fileName = file.getOriginalFilename();
            //2.上传
                //2.1 通过工具类产生新图片名称，防止重名
            String picNewName = FileNameUtils.generateRandonFileName(fileName);
                //2.2 设置图片存储在服务器中的目录 例如：xx/face/userId/xxx.jpg
            String picSavePath = "face/"+userId;
            log.info("----最新图片名称："+picNewName+"----最新图片存储路径："+picSavePath);
                //2.3 上传到服务器
            SFtpUtil.uploadFile(picSavePath,picNewName,file.getInputStream(),sFtpConfig);
            //3、设置本地访问服务器对应图片的地址
            String picAddressUrl = sFtpConfig.getImageBaseUrl()+picSavePath+"/"+picNewName;
            log.info("浏览器访问服务器中对应图片的Url："+picAddressUrl);
           //4、保存图片的路径到数据库
            myCenterService.updateUserInfoOfFaceByUserId(userId,picAddressUrl);
            result = "上传成功";
            usersVo.setFace(picAddressUrl);
            //更换完图片后就把最新的头像图片地址写回Cookie中
            CookieUtils.setCookie(request,response,"user",objectMapper.writeValueAsString(usersVo),604800,true);
            return JsonResult.isOk(result);
        } catch (Exception e) {
            e.printStackTrace();
            result = "服务器出现错误，上传失败";
            return JsonResult.isErr(500,result);
        }
    }

}
