package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Album;
import com.qingcheng.pojo.goods.UploadAlbumImage;
import com.qingcheng.service.goods.AlbumService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Reference
    private AlbumService albumService;

    @GetMapping("/findAll")
    public List<Album> findAll(){
        return albumService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Album> findPage(int page, int size){
        return albumService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Album> findList(@RequestBody Map<String,Object> searchMap){
        return albumService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Album> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  albumService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Album findById(Long id){
        Album album = albumService.findById(id);
        return album;
    }


    @PostMapping("/add")
    public Result add(@RequestBody Album album){
        albumService.add(album);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Album album){
        albumService.update(album);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        albumService.delete(id);
        return new Result();
    }

    @PostMapping("/saveImage") // 保存图片列表
    public Result saveImage(@RequestBody List<UploadAlbumImage> list,String albumId){ // 传递所有图片的url
        this.albumService.saveImage(albumId,list);
        return new Result();
    }

    @PostMapping("/deleteImage") // 删除图片列表
    public Result deleteImage(String albumId , String uid){
        this.albumService.deleteImage(albumId,uid);

        return new Result();
    }

}
