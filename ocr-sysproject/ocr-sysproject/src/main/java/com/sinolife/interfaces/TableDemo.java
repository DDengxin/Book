package com.sinolife.interfaces;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface TableDemo {
    //体检费用清单识别方法
    JSONObject checkUpCost(String base64String);
    //交通费及误餐费报销清单识别方法
    JSONObject  jiaoTongWuCanFei(String base64String);
    //外勤差旅费用明细表识别方法
    JSONObject waiQingChaiLv(String base64String);
    //会议（培训）费用结算明细表识别方法
    JSONObject huiYiFeiYong(String base64String);
    //车辆费用报销清单识别方法
    JSONObject carCost(String base64String);
    //差旅费用明细表识别方法
    JSONObject chaiLv(String base64String);
    //手续费汇总计算表识别方法
    JSONObject shouXuFei(String base64String);
    //业务方案支出管理台账识别方法
    JSONObject yeWu(String base64String);
    //会议培训签到表
    JSONObject meetingSignIn(String base64String);
    //采购申请单
    JSONObject purchaseRequisition(String type,String base);
    //理赔查勘费用清单(分/支公司适用)
    JSONObject claimsSurvey(String base);
    //招待费用明细(总/分公司适用)
    JSONObject entertainmentExpenses(String baseStr);
    //个人所得税代扣代缴
    JSONObject individualInconmeTax(String type, String base);
    //邮寄费用明细清单
    JSONObject mailCost(String base64String);
    //费用分摊表
    JSONObject costShare(String base64String);
    //方案达成明细表
    JSONObject schemeReach(String base64String);
    //评标汇总表
    JSONObject evaluationSummary(String base);
    //工会经费计提表
    JSONObject tradeUnion(String base);
}
