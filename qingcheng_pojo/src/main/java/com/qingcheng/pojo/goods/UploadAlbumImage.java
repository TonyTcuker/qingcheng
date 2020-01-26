package com.qingcheng.pojo.goods;

import java.io.Serializable;

/**
 * 这个类保存图片上传传递过来的uuid与数据
 */
public class UploadAlbumImage implements Serializable {
    private String uid ;
    private String url ;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
