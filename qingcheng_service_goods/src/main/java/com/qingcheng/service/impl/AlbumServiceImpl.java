package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.cj.util.StringUtils;
import com.qingcheng.dao.AlbumMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Album;
import com.qingcheng.pojo.goods.UploadAlbumImage;
import com.qingcheng.service.goods.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import xyz.downgoon.snowflake.Snowflake;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = AlbumService.class)
@Transactional
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private Snowflake snowflake ;

    @Autowired
    private OSSClient ossClient;


    /**
     * 返回全部记录
     * @return
     */
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Album> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Album> albums = (Page<Album>) albumMapper.selectAll();
        return new PageResult<Album>(albums.getTotal(),albums.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Album> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return albumMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Album> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Album> albums = (Page<Album>) albumMapper.selectByExample(example);
        return new PageResult<Album>(albums.getTotal(),albums.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Album findById(Long id) {
        Album album = albumMapper.selectByPrimaryKey(id);
        return album;
    }

    /**
     * 新增
     * @param album
     */
    public void add(Album album) {
        album.setImageItems("[]"); // 新增的时候需要初始化图片列表，否则前台页面解析json会出错
        albumMapper.insert(album);
    }

    /**
     * 修改
     * @param album
     */
    public void update(Album album) {
        albumMapper.updateByPrimaryKeySelective(album);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    /**
     * 实现图片的保存，保存格式为[{uid:url}...]
     * @param imageList 传递过来的url地址
     */
    @Override
    public void saveImage(String albumId,List<UploadAlbumImage> imageList) {
        for (UploadAlbumImage image:imageList){ // 把没有uid的图片，创建一个uid
            if (StringUtils.isNullOrEmpty(image.getUid())){ // uid为空的图片
                image.setUid(this.snowflake.nextId()+""); // 创建uid
            }
        }
        String imageListJson = JSONObject.toJSONString(imageList);

        Album album = new Album();
        album.setId(Long.valueOf(albumId)); // 设置ID
        album.setImageItems(imageListJson); // 设置图片
        this.albumMapper.updateByPrimaryKeySelective(album);
    }

    /**
     * 实现列表图片删除
     * @param uid 需要删除都图片uID
     */
    @Override
    public void deleteImage(String albumId ,String uid) {
        String bucketName = "qingcheng-dianshang01";


        Album album = this.albumMapper.selectByPrimaryKey(albumId); //查询数据
        List<UploadAlbumImage> albumList = JSON.parseArray(album.getImageItems(), UploadAlbumImage.class); // 将图片Json字符串转换成list对象
        List<UploadAlbumImage> images = albumList ;//如果遍历和删除同时进行会出现锁的问题，所以重新定义变量进行删除

        Iterator<UploadAlbumImage> iterator = albumList.iterator();
        while(iterator.hasNext()){
            UploadAlbumImage image = iterator.next();
            if (image.getUid().equals(uid)){
                //oss文件删除 路径+文件名 不需要根目录的/
                // 例子 test/album/1/123.jpg
                this.ossClient.deleteObject(bucketName,image.getUrl().replace("https://qingcheng-dianshang01.oss-cn-shenzhen.aliyuncs.com/",""));
                images.remove(image); // 删除掉指定uid的图片
                break;
            }
        }

        String imagesJson = JSONObject.toJSONString(images); // List转换成json字符串
        album.setImageItems(imagesJson);//重新设置参数
        this.albumMapper.updateByPrimaryKeySelective(album);//更新数据
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 相册名称
            if(searchMap.get("title")!=null && !"".equals(searchMap.get("title"))){
                criteria.andLike("title","%"+searchMap.get("title")+"%");
            }
            // 相册封面
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("imageItems")!=null && !"".equals(searchMap.get("imageItems"))){
                criteria.andLike("imageItems","%"+searchMap.get("imageItems")+"%");
            }


        }
        return example;
    }

}
