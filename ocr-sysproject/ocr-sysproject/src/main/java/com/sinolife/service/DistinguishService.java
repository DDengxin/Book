
//识别
package com.sinolife.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.enums.ResultEnum;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.interfaces.TickeDemo;
//import com.sinolife.sf.esb.EsbMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class DistinguishService {

    private String code403 = ResultEnum.CODE403.getResult();
    private String code200 = ResultEnum.CODE200.getResult();
    private String success = ResultEnum.SUCCESS.getResult();
    private String fail = ResultEnum.FAIL.getResult();


    private  static final Logger logger= LoggerFactory.getLogger(DistinguishService.class);

    @Autowired
    private TableDemo tableDemo;

    @Autowired
    private TickeDemo tickeDemo;

    @Autowired
    private OcrService ocrService;


	/**
	*识别接口
	*/
//    @EsbMethod(esbServiceId = "com.sinolife.ocr.esb.distinguish")
    public String distinguish(Map<String,String> map){
        JSONObject object = new JSONObject(true);
        JSONArray array = new JSONArray();
        JSONObject job = new JSONObject(true);
        long start = System.currentTimeMillis();
        try {
        if (map.get("image") != null) {
            String base64String = map.get("image");
            if (map.get("type") == null) {
                String ocr = ocrService.ocr(map);
                JSONObject jsonObject = JSONObject.parseObject(ocr);
                JSONArray item = jsonObject.getJSONArray("item");
                if (item != null) {
                    for (int i = 0; i < item.size(); i++) {
                        String type = item.getJSONObject(i).getString("type");
                        object = shiBie(type, base64String);
                        array.add(object);
                    }
                    job.put("code", code200);
                    job.put("msg", success);
                    job.put("result", array);
                }
            }else {
                object = shiBie(map.get("type"), base64String);
                array.add(object);
                job.put("code", code200);
                job.put("msg", success);
                job.put("result", array);
            }
        }else {
            job.put("code",code403);
            job.put("msg",fail);
        }

        } catch (Exception e) {
            e.printStackTrace();
            job.put("code",code403);
            job.put("msg",e.getMessage());
        }finally {
            logger.warn("所有识别完成耗时："+(System.currentTimeMillis()-start));
            return job.toJSONString();
        }
    }


    private JSONObject shiBie(String type,String base64String){
        JSONObject object = new JSONObject(true);
        if (type!= null) {
            if (type.contains("体检费用清单")) {
                //体检费用清单识别
                object = tableDemo.checkUpCost(base64String);
            } else if (type.contains("交通费及误餐费报销清单")) {
                //交通费及误餐费报销清单识别
                object = tableDemo.jiaoTongWuCanFei(base64String);
            } else if (type.contains("外勤差旅费用明细表")) {
                //外勤差旅费用明细表识别
                object = tableDemo.waiQingChaiLv(base64String);
            } else if (type.contains("会议(培训)费用结算明细表")) {
                //会议(培训)费用结算明细表识别
                object = tableDemo.huiYiFeiYong(base64String);
            } else if (type.contains("车辆费用报销清单")) {
                //车辆费用报销清单识别
                object = tableDemo.carCost(base64String);
            } else if (type.contains("差旅费用明细表")) {
                //差旅费用明细表识别
                object = tableDemo.chaiLv(base64String);
            } else if (type.contains("手续费汇总计算表")) {
                //差旅费用明细表识别
                object = tableDemo.shouXuFei(base64String);
            } else if (type.contains("业务方案支出管理台账")) {
                //差旅费用明细表识别
                object = tableDemo.yeWu(base64String);
            } else if (type.contains("会议(培训)签到表")) {
                //会议(培训)签到表
                object = tableDemo.meetingSignIn(base64String);
            } else if (type.contains("采购申请单")) {
                //采购申请单
                object = tableDemo.purchaseRequisition(type,base64String);
            }else if (type.contains("理赔查勘费用清单")) {
                //理赔查勘费用清单(分/支公司适用)
                object = tableDemo.claimsSurvey(base64String);
            }else if (type.contains("招待费明细表")) {
                //招待费用明细表
                object = tableDemo.entertainmentExpenses(base64String);
            }else if (type.contains("个人所得税代扣代缴申请表")) {
                //采购申请单
                object = tableDemo.individualInconmeTax(type,base64String);
            }else if (type.contains("邮寄费用")) {
                //邮寄费用明细表
                object = tableDemo.mailCost(base64String);
            }else if (type.contains("费用分摊")) {
                //费用分摊表
                object = tableDemo.costShare(base64String);
            }else if (type.contains("方案达成明细")) {
                //方案达成明细表
                object = tableDemo.schemeReach(base64String);
            }else if (type.contains("评标汇总表")) {
                //评标汇总表
                object = tableDemo.evaluationSummary(base64String);
            }else if (type.contains("工会经费计提")) {
                //工会经费计提表
                object = tableDemo.tradeUnion(base64String);
            }else if (type.contains("quota")) {
                //定额发票
                object = tickeDemo.quotaHttp(base64String);
            } else if (type.contains("toll")) {
                //车辆通行费
                object = tickeDemo.tollHttp(base64String);
            } else if (type.contains("taxi")) {
                //出租车发票
                object = tickeDemo.taxiHttp(base64String);
            } else if (type.contains("train")) {
                //火车票
                object = tickeDemo.trainHttp(base64String);
            } else if (type.contains("mvs")) {
                //机动车销售发票
                object = tickeDemo.mvsHttp(base64String);
            } else if (type.contains("flight")) {
                //飞机行程单
                object = tickeDemo.flightHttp(base64String);
            } else if (type.contains("营业执照")) {
                //营业执照
                object = tickeDemo.licenseHttp(base64String);
            } else if (type.contains("passenger")) {
                //客运车票
                object = tickeDemo.passengerHttp(base64String);
            } else if (type.contains("special")) {
                //增值税发票
                object = tickeDemo.vatHttp(base64String);
            } else if (type.contains("normal")) {
                //增值税发票
                object = tickeDemo.vatHttp(base64String);
            } else if (type.contains("electronic")) {
                //增值税发票
                object = tickeDemo.vatHttp(base64String);
            } else if (type.contains("roll")) {
                //增值税发票
                object = tickeDemo.vatHttp(base64String);
            } else if (type.contains("printed")) {
                //通用机打发票
                object = tickeDemo.printedHttp(base64String);
            } else if (type.contains("ship")) {
                //船票
                object = tickeDemo.shipHttp(base64String);
            } else if (type.contains("blockchain")) {
                //深圳区块链发票
                object = tickeDemo.vatHttp(base64String);
                object.put("type", "blockchain_invoice");
            } else if (type.contains("支付宝")) {
                //支付宝品质
                object = tickeDemo.AliPayBills(base64String);
            } else if (type.contains("微信")) {
                //微信凭证
                object = tickeDemo.WeChatBills(base64String);
            } else if (type.contains("银行回单")) {
                //银行回单
                object = tickeDemo.bankErceipt(base64String);
            } else if (type.contains("网银支付")) {
                //网银支付截图
                object = tickeDemo.internetBanking(base64String);
            } else if (type.contains("刷卡小票")) {
                //刷卡小票
                object = tickeDemo.brushCard(base64String);
            }
        }
        return object;
    }
}
