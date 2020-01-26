package com.qingcheng.controller.util;


import java.util.HashMap;
import java.util.Map;

/**
 * 后台流量统计流量统计类
 */
public class TrafficStatistics {

    private static TrafficStatistics trafficStatistics ;

    private int pVCount; // 点击次数
    private Map<String,String> uVMap; // 流量统计


    private TrafficStatistics() {
        this.pVCount = 0 ;
        this.uVMap = new HashMap<>();
    }

    public static TrafficStatistics getTrafficStatistics(){
        if (trafficStatistics==null){
            synchronized (TrafficStatistics.class){
                if (trafficStatistics==null){
                    trafficStatistics = new TrafficStatistics();
                }
            }
        }
        return trafficStatistics ;
    }

    public void addUvMap(String name,String value){
        String data = this.uVMap.get(name);
        if (data==null){ // 没有使用过
            this.uVMap.put(name,value);
        }
    }

    public void addPvCount(){ // 统计点击数
        this.pVCount ++ ;
    }

    public Integer getCountUv(){ // 返回人数流量统计数
        return this.uVMap.size();
    }

    public Integer getCountPv(){ // 返回统计的点击数
        return this.pVCount ;
    }

    public void deleteAllUv(){ // 清空Uv
        this.uVMap.clear(); // 清空集合
    }
    public void deleteAllPv(){ // 清空点击数
        this.pVCount = 0 ;
    }
}
