package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.pojo.system.Resource;
import com.qingcheng.service.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public List<Spu> findAll(){
        return spuService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Spu> findPage(int page, int size){
        return spuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Spu> findList(@RequestBody Map<String,Object> searchMap){
        return spuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Spu> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  spuService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Spu findById(String id){
        return spuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Spu spu){
        spuService.update(spu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id){
        this.spuService.delete(id);
        return new Result();
    }

    @PostMapping("/save")
    public Result saveGoods(@RequestBody Goods goods){
        this.spuService.saveGoods(goods);
        return new Result();
    }

    @GetMapping("/findGoodsById")
    public Goods findGoodsById(String id){
        Goods goods = this.spuService.findGoodsById(id);
        return goods ;
    }

    @PostMapping("/audit") // 商品审核
    public Result audit(@RequestBody Map<String,String> map){
        String id = map.get("id");
        String status = map.get("status");
        String message = map.get("message");
        this.spuService.audit(id,status,message); // 执行审核
        return new Result(); // 返回成功执行信息
    }

    @PostMapping("/pull") // 商品下架
    public Result pull(String id){
        this.spuService.pull(id);
        return new Result();
    }

    @PostMapping("/put")
    public Result put(String id){
        this.spuService.put(id);
        return new Result();
    }

    @PostMapping("/putMany") // 批量上架
    public Result putMany(String[] ids){
        int count = this.spuService.putMany(ids);
        return new Result(0,"上架"+count+"商品");// 0 执行成功
    }

    @PostMapping("/pullMany") //批量下架
    public Result pullMany(String[] ids){
        int count = this.spuService.pullMany(ids);
        return new Result(0,"下架"+count+"商品");
    }

    @PostMapping("/logicDelete") // 逻辑删除数据
    public Result logicDelete(String id){
        this.spuService.logicDelete(id);
        return new Result();
    }

    @PostMapping("/restoreSpu")// 还原删除的数据
    public Result restoreSpu(String id){
        this.spuService.restoreSpu(id);
        return new Result();
    }
}
