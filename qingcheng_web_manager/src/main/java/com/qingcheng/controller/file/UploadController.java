package com.qingcheng.controller.file;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/native")
    public String nativeUpload(@RequestParam("file") MultipartFile file) {
        String path=request.getSession().getServletContext().getRealPath("img");
        String filePath = path +"/"+ file.getOriginalFilename();
        File desFile = new File(filePath);
        if(!desFile.getParentFile().exists()){
            desFile.mkdirs();
        }
        try {
            file.transferTo(desFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("path:---"+filePath);
        return "http://localhost:19000/img/"+file.getOriginalFilename();
    }

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file,String folder){
        String bucketName = "qing-cheng";
        String fileName= folder+"/"+ UUID.randomUUID()+"_"+file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://"+bucketName+"."+ ossClient.getEndpoint().toString().replace("http://","") +"/"+fileName;
    }

    /**
     * 保存到阿里云端
     * @param file 上传的文件
     * @param fileFolder 文件路径
     * @return
     */
    @PostMapping("/oss2")
    public String ossUpload2(@RequestParam("file") MultipartFile file,String fileFolder){
        String bucketName = "qingcheng-dianshang01";
        String fileName = fileFolder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename(); // 文件名称
        try {
            this.ossClient.putObject(bucketName,fileName,file.getInputStream()); // 上传文件
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }
        //this.ossClient.getEndpoint().toString() 返回 http://oss-cn-shenzhen.aliyuncs.com
        String path = "https://"+bucketName+"."+this.ossClient.getEndpoint().toString().replace("http://","")+"/"+fileName;
        return path;
    }

}
