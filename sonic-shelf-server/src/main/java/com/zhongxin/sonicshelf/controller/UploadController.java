package com.zhongxin.sonicshelf.controller;

import cn.hutool.core.io.FileUtil;

import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.mapper.AlbumMapper;
import com.zhongxin.sonicshelf.mapper.ArtistMapper;
import com.zhongxin.sonicshelf.mapper.MusicMapper;
import com.zhongxin.sonicshelf.service.PlaylistService;
import com.zhongxin.sonicshelf.service.UserService;
import com.zhongxin.sonicshelf.util.JwtUtil;
import com.zhongxin.sonicshelf.util.Result;
import com.zhongxin.sonicshelf.util.TokenExtractor;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Resource
    UserService userService;
    @Resource
    PlaylistService playlistService;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String filePath = System.getProperty("user.dir") + "/files/uploads/userAvatars/";

        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        userService.updateUserAvatar("/uploads/userAvatars/" + fileName, token);

        return Result.success("上传成功", "/uploads/userAvatars/" + fileName);
    }

    @PostMapping("/cover/{id}")
    public Result uploadCover(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {
        String filePath = System.getProperty("user.dir") + "/files/uploads/playlistCovers/";

        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        playlistService.updatePlaylistCover("/uploads/playlistCovers/" + fileName, id);
        FileUtil.writeBytes(bytes, filePath + fileName);

        return Result.success("上传成功", "/uploads/playlistCovers/" + fileName);
    }

    @PostMapping("/artistCover/{id}")
    public Result uploadArtistCover(@RequestParam("file") MultipartFile file, @PathVariable Long id, HttpServletRequest request) throws IOException {
        String token = TokenExtractor.extractToken(request);
        if (!jwtUtil.isAdmin(token)) {
            return Result.error("需要管理员权限");
        }

        String filePath = System.getProperty("user.dir") + "/files/cover/artistCover/";
        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);

        artistMapper.uploadArtistCover("/cover/artistCover/" + fileName, id);

        return Result.success("上传成功", "/cover/artistCover/" + fileName);
    }

    @AdminAuth
    @PostMapping("/albumCover/{id}")
    public Result uploadAlbumCover(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {

        String filePath = System.getProperty("user.dir") + "/files/cover/albumCover/";
        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);

        albumMapper.uploadArtistCover("/cover/albumCover/" + fileName, id);

        return Result.success("上传成功", "/cover/albumCover/" + fileName);
    }

    @AdminAuth
    @PostMapping("/musicCover/{id}")
    public Result uploadMusicCover(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {

        String filePath = System.getProperty("user.dir") + "/files/cover/musicCover/";
        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);

        musicMapper.uploadMusicCover("/cover/musicCover/" + fileName, id);

        return Result.success("上传成功", "/cover/musicCover/" + fileName);
    }

    @AdminAuth
    @PostMapping("/musicFile/{id}")
    public Result uploadMusicFile(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {

        String filePath = System.getProperty("user.dir") + "/files/music/";
        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        FileUtil.writeBytes(bytes, filePath + fileName);

        musicMapper.uploadMusicFile("/music/" + fileName, id);

        return Result.success("上传成功", "/music/" + fileName);
    }

    @AdminAuth
    @PostMapping("/playlistCover/{id}")
    public Result uploadPlaylistCover(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {

        String filePath = System.getProperty("user.dir") + "/files/uploads/playlistCovers/";

        byte[] bytes = file.getBytes();
        // 使用时间戳前缀避免文件名冲突，直接使用原始文件名（包含中文）
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        playlistService.updateOfficialPlaylistCover("/uploads/playlistCovers/" + fileName, id);
        FileUtil.writeBytes(bytes, filePath + fileName);

        return Result.success("上传成功", "/uploads/playlistCovers/" + fileName);
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
