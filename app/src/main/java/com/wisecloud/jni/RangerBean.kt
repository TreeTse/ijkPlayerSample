package com.wisecloud.jni

import java.io.Serializable


/**
 * 中间件Call方法返回
 * @param err 错误码
 * @param res 逻辑方法返回值,非err时约定的值
 */
data class RangerResult(var err: Int, var res: String)

/**
 * @param program_code:"dfws",   节目code，从节目单取值，全局唯一 ，即直播：channelcode, , 点播回看：contentId
 * @param buss:"live",   業務類型(live/vod/rec(回看))
 * @param desc:"dfws",   当前播放的节目名称
 * @param sources: 一路可播放的媒体源
 * @param quality 480p,720p,1080p,4K
 * @param delay:1000000000  延時時間，單位ns，中间件暂时没用上，传固定值
 * @param timeout:2000000000  超时时间，单位ns ,传api/portalCore/v#/getSlbInfo接口响应的switchLiveSourceTimeV2 字段的截取逗号后面的值，记得单位换算成ns
 * @param start 起播时刻（适用于点播）
 * @param lang 语言标签（此字段暂可选，默认空值）
 */
data class ProgramInfo(var program_code: String,
                       var buss: String,
                       var desc: String,
                       var quality: String,
                       var delay: Long,
                       var timeout: Long,
                       var sources: List<Sources>,
                       var start :Long = 0,
                       var lang:String = "",
                       var app_ctx:String): Serializable

/**
 * @param priority 0 媒资源序号，优先级 ,传api/portalCore/v#/getSlbInfo接口响应的serial_number
 * @param media_code "dfws-1",  媒资mediacode
 * @param main_addr   传api/portalCore/v#/getSlbInfo接口响应的main_addr
 * @param spared_addr 传api/portalCore/v#/getSlbInfo接口响应的spared_addr
 * @param auth  原请求cdn时的源，直播：传api/portalCore/v#/getSlbInfo接口响应的url_list数组的根据tag匹配的url值 ；
 * @param license 直播流License，传节目单直播栏目上架数据接口api/portalCore/v#/getLiveData的license，点播传接口（）返回的 license
 * @param tag  cdn源类型， 直播：传getslbinfo接口响应的cdn_type ，点播(api/portalCore/v#/startPlayVOD)回看(api/portalCore/v#/startPlayBTV)：接口响应的 cdnType
 * @param quality  480p,720p,1080p,4K
 * @param main_addr_code  传api/portalCore/v#/getSlbInfo接口响应的 main_addr_mark
 * @param spared_addr_code  传api/portalCore/v#/getSlbInfo接口响应的 spared_addr_mark
 * @param lang   语言标签，需与外层lang字段值相同此资源方可用（此字段暂可选，默认空值）,点播多流多音轨时使用
 * @param id    源ID（此字段暂可选）
 * @param id_code 源ID code，即cdn_id_mark）  getslbinfo 接口返回
 * @param rule_id_code 策略ID code，即rule_id_mark
 * @param weight 源权重，越大越优先
 */
data class Sources(var priority: Int,
                   var media_code: String,
                   var main_addr: String,
                   var spared_addr: String,
                   var auth: String,
                   var license: String,
                   var tag: String,
                   var quality: String,
                   var main_addr_code:String,
                   var spared_addr_code:String,
                   var lang : String = "",
                   var id:String = "",
                   var id_code:String?,
                   var rule_id_code:String?,
                   var weight:Int)