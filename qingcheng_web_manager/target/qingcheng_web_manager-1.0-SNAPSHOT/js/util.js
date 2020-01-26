
// 返回页面url参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = window.location.search.substr(1).match(reg);
    if (r!=null) return unescape(r[2]); return null;
}

//日期格式化"2017-07-05"
function formatTime(date) {
    var year = date.getFullYear();
    var month = date.getMonth() + 1, month = month < 10 ? '0' + month : month;
    var day = date.getDate(), day = day < 10 ? '0' + day : day;
    return year + '-' + month + '-' + day;
}

/**
 * 返回日期数组
 * @param day 指定天数
 */
function getDateList(day) {
    let DateLis=[];
    for (let i=0;i<day;i++){
        let date = new Date();
        //获取当前时间减去i天，获得时间戳，在重新设置Date对象
        date.setDate(new Date().getDate()-i);

        //倒序插入
        DateLis.unshift(formatTime(date));
    }
    return DateLis ;
}

/**
 * 去除yyyy-MM-dd HH:mm:ss 时间
 * @param DateTime 日期时间字符串
 */
function deleteDateTime(DateTime) {
    return DateTime.substr(0,10); // 截取0-10的字符串
}