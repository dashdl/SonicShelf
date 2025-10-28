package com.zhongxin.sonicshelf.controller;

import cn.hutool.core.io.FileUtil;
import com.zhongxin.sonicshelf.dto.response.Result;
import com.zhongxin.sonicshelf.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Resource
    UserService userService;

    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String filePath = System.getProperty("user.dir") + "/files/uploads/avatars/";

        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        userService.updateUserAvatar("/uploads/avatars/" + fileName, token);

        return Result.success("上传成功", "/uploads/avatars/" + fileName);
    }

//    @GetMapping("/download/{filName}")
//    public void download(@PathVariable("filName") String filName, HttpServletResponse response) throws IOException {
//        String filePath = System.getProperty("user.dir")+"/files/";
//        String realPath = filePath+filName;
//        boolean exists = FileUtil.exist(realPath);
//        if (!exists) {
//            throw new CustomException("文件不存在");
//        }
//        byte[] data = FileUtil.readBytes(realPath);
//        ServletOutputStream out = response.getOutputStream();
//        out.write(data);
//        out.flush();
//        out.close();
//    }
}
