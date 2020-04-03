package com.sinolife.service;


//分类识别
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sinolife.properties.CflProperties;
import com.sinolife.properties.TableProperties;
import okhttp3.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(value = CflProperties.class)
public class Classify {

	private  static final Logger logger= LoggerFactory.getLogger(OcrService.class);

	@Autowired
	private CflProperties cflProperties;

	/**
	 * 表格规则路由
	 * @param items 所有关键字
	 * @return   类型
	 */
	private  static  String TablesRule(List<String> items){
		String type=null;
		if (!items.isEmpty()) {
			//items.forEach((words)-> System.out.println("words = "+words));
					//评标汇总表
					if (items.toString().contains("评标人签字")
							&& items.toString().contains("平均分值")
							&& items.toString().contains("评标人")
							&& items.toString().contains("评标汇总表")) {
						type = "评标汇总表";
					}
					//采购申请单中支
					if (items.toString().contains("采购申请单")
							&& items.toString().contains("中支公司适用")
							&& (items.toString().contains("申请人姓名")
							|| items.toString().contains("申请人")
							|| items.toString().contains("申请"))
							&& items.toString().contains("中支")) {
						type = "采购申请单(中支公司适用)";
					}
					//采购申请单总分公司适用
					if (items.toString().contains("采购申请单")
							&& items.toString().contains("总/分公司适用")
							&& (items.toString().contains("申请人姓名")
							|| items.toString().contains("申请人")
							|| items.toString().contains("申请"))
							&& items.toString().contains("分公司")) {
						type = "采购申请单(总/分公司适用)";
					}
					//采购申请单集团适用
					if (items.toString().contains("采购申请单")
							&& items.toString().contains("集团适用")
							&& (items.toString().contains("申请人姓名")
							|| items.toString().contains("申请人")
							|| items.toString().contains("申请"))
							&& items.toString().contains("中心分管总")) {
						type = "采购申请单(集团适用)";
					}
					//合并收入计算个人所得税确认表
					if (items.toString().contains("合并收入计算个人所得税确认表")
							&&items.toString().contains("税事项")
							&&items.toString().contains("税总额")
							&&items.toString().contains("税人员")){
						type = "合并收入计算个人所得税确认表";
					}
			         //特殊事项申请表(集团适用)
			         if (items.toString().contains("特殊事项申请表")
			         		&&items.toString().contains("集团适用")
			         		&&items.toString().contains("报销单号")
			         		&&items.toString().contains("总经理")){
			         	type = "特殊事项申请表(集团适用)";
			         }
			         //特殊事项申请表(支公司适用)
			         if (items.toString().contains("特殊事项申请表")
			         		&&items.toString().contains("支公司适用")
			         		&&items.toString().contains("报销单号")
			         		&&items.toString().contains("中支公司负责人")){
			         	type = "特殊事项申请表(支公司适用)";
			         }
					//特殊事项申请表(总公司适用)
					if (items.toString().contains("特殊事项申请表")
							&&items.toString().contains("总公司适用")
							&&items.toString().contains("报销单号")
							&&items.toString().contains("总公司财务负责人")){
						type = "特殊事项申请表(总公司适用)";
					}
					//特殊事项申请表(分公司适用)
					if (items.toString().contains("特殊事项申请表")
							&&items.toString().contains("分公司适用")
							&&items.toString().contains("报销单号")
							&&items.toString().contains("分公司部门负责人")){
						type = "特殊事项申请表(分公司适用)";
					}

					//费用分摊表
					if (items.toString().contains("费用分摊表")
							&& items.toString().contains("费用")
							&& items.toString().contains("预算中心")
							&& items.toString().contains("成本中心")
							&& items.toString().contains("预算科目")
							&& items.toString().contains("分摊")) {
						type = "费用分摊表";
					}

					//会议培训签到表
					if (items.toString().contains("通知/签到表")
							&& items.toString().contains("会议（培训）")
							&& items.toString().contains("经办人")
							&& items.toString().contains("会议")
							&& items.toString().contains("培训")) {
						type = "会议(培训)签到表";
					}
					//工会经费计提表
					if (items.toString().contains("工会经费计提表")
							&& items.toString().contains("机构名称")
							&& items.toString().contains("单位部分")
							&& items.toString().contains("个人部分")
							&& items.toString().contains("工资渠道")) {
						type = "工会经费计提表";
					}
					//体检照会单
					if (items.toString().contains("体检照会")
							&& items.toString().contains("医院")
							&& items.toString().contains("客户姓名")
							&& items.toString().contains("体检")) {
						type = "体检照会单";
					}
					//个人所得税代扣代缴申请表(中支公司适用)
					if (items.toString().contains("个人所得税代扣代缴申请")
							&& items.toString().contains("扣代缴申请表")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("应纳税所得额")
							&& items.toString().contains("应纳税额")
							&& items.toString().contains("发票报销金额")) {
						type = "个人所得税代扣代缴申请表(中支公司适用)";
					}
					//个人所得税代扣代缴申请表(总分公司适用)
					if (items.toString().contains("个人所得税代扣代缴申请")
							&& items.toString().contains("扣代缴申请表")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("应纳税所得额")
							&& items.toString().contains("应纳税额")
							&& items.toString().contains("发票报销金额")) {
						type = "个人所得税代扣代缴申请表(总/分公司适用)";
					}
					//电销坐席费结算表
					if (items.toString().contains("电销分中心")) {
						type = "电销坐席费结算表";
					}
					//业务方案支出管理台账
					if (items.toString().contains("业务方案支出管理台账")
							&& items.toString().contains("方案主题")
							&& items.toString().contains("方案编号")) {
						type = "业务方案支出管理台账";
					}
					//方案
					if (items.toString().contains("方案")
							&& items.toString().contains("富保寿保费")
							&& items.toString().contains("各分公司")) {
						type = "方案";
					}
					//会议培训费用结算明细表(中支公司适用)
					if (items.toString().contains("费用结算明细表")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("会议/培训内容")
							&& items.toString().contains("会议/培训时间")
							&& items.toString().contains("会议/培训地点")
							&& items.toString().contains("费用项目")
							&& items.toString().contains("票据序号")) {
						type = "会议(培训)费用结算明细表(中支公司适用)";
					}
					//会议培训费用结算明细表(总分公司适用)
					if (items.toString().contains("费用结算明细表")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("会议/培训内容")
							&& items.toString().contains("会议/培训时间")
							&& items.toString().contains("会议/培训地点")
							&& items.toString().contains("费用项目")
							&& items.toString().contains("票据序号")) {
						type = "会议(培训)费用结算明细表(总/分公司适用)";
					}
					//会议通知
					if (items.toString().contains("通知")
							&& items.toString().contains("现将有关事宜通知如下")) {
						type = "会议通知";
					}
					//外勤差旅费用明细表(分公司适用)
					if (items.toString().contains("外勤差旅费用明细表")
							&& items.toString().contains("分公司适用")
							&& items.toString().contains("出差事由")
							&& items.toString().contains("出差业务开始日期")
							&& items.toString().contains("项目")
							&& items.toString().contains("出差业务结束日期")) {
						type = "外勤差旅费用明细表(分公司适用)";
					}
					//邮寄费用明细清单
					if (items.toString().contains("邮寄费用明细清单")
							&& items.toString().contains("寄件")
							&& items.toString().contains("收件")
							&& items.toString().contains("寄件人")) {
						type = "邮寄费用明细清单";
					}
					//内请1
					if (items.toString().contains("富德生命-支公司-支公司上报分公司请示")
							&& items.toString().contains("公文流水号")
							&& items.toString().contains("拟稿机构")) {
						type = "内请";
					}
					//内请2
					if (items.toString().contains("公文流水号")
							&& items.toString().contains("拟稿日期")
							&& items.toString().contains("拟稿机构")) {
						type = "内请";
					}
					//签收表
					if (items.toString().contains("签收表")) {
						type = "签收表";
					}
					//外勤差旅费用明细表(中支使用)
					if (items.toString().contains("外勤差旅费用明细表")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("出差事由")
							&& items.toString().contains("出差目的地")) {
						type = "外勤差旅费用明细表(中支公司适用)";
					}
					//招待费用明细表
					if (items.toString().contains("招待费明细表")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("制表人")) {
						type = "招待费明细表(总/分公司适用)";
					}
					//体检费用清单
					if (items.toString().contains("体检费用清单")
							&& items.toString().contains("体检人")
							&& items.toString().contains("投保")) {
						type = "体检费用清单";
					}
					//手续费汇总计算表
					if (items.toString().contains("手续费汇总计算表")
							&& items.toString().contains("机构")
							&& items.toString().contains("渠道")) {
						type = "手续费汇总计算表";
					}
					//交通费及误餐费报销清单(中支公司适用)
					if (items.toString().contains("交通费及误餐费报销清单")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("票据序号")
							&& items.toString().contains("经办人")) {
						type = "交通费及误餐费报销清单(中支公司适用)";
					}
					//交通费及误餐费报销清单(总/分公司适用)
					if (items.toString().contains("交通费及误餐费报销清单")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("票据序号")
							&& items.toString().contains("经办人")) {
						type = "交通费及误餐费报销清单(总/分公司适用)";
					}
					//理赔查勘费用清单(分支公司适用)
					if (items.toString().contains("理赔查勘费用清单")
							&& items.toString().contains("分/支公司适用")
							&& items.toString().contains("票据序号")) {
						type = "理赔查勘费用清单(分/支公司适用)";
					}
					//车辆费用报销清单(总/分公司适用)
					if (items.toString().contains("车辆费用报销清单")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("车辆使用费")) {
						type = "车辆费用报销清单(总/分公司适用)";
					}
					//车辆费用报销清单(中支公司适用)
					if (items.toString().contains("车辆费用报销清单")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("车辆使用费")) {
						type = "车辆费用报销清单(中支公司适用)";
					}
					//入会缴费通知
					if (items.toString().contains("行业协会")
							&& items.toString().contains("通知")
							&& items.toString().contains("国务院")) {
						type = "入会缴费通知";
					}
					//差旅费用明细表(总/分公司适用)
					if (items.toString().contains("差旅费用明细表")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("出差人员")) {
						type = "差旅费用明细表(总/分公司适用)";
					}
					//方案达成明细表
					if (items.toString().contains("方案达成明细表")
							&& items.toString().contains("达成人员工号")
							&& items.toString().contains("方案考核业绩")) {
						type = "方案达成明细表";
					}
					//发票分割单(总/分公司适用)
					if (items.toString().contains("发票分割单")
							&& items.toString().contains("总/分公司适用")
							&& items.toString().contains("分割原因")
							&& items.toString().contains("发票分割项数")) {
						type = "发票分割单(总/分公司适用)";
					}
					//发票分割单(中支公司适用)
					if (items.toString().contains("发票分割单")
							&& items.toString().contains("中支公司适用")
							&& items.toString().contains("分割原因")
							&& items.toString().contains("发票分割项数")) {
						type = "发票分割单(中支公司适用)";
					}
			}
			return type;
		}

	/**
	 * 其他规则路由
	 * @param items
	 * @return
	 */
	private  static  String otherRule(List<String> items){
		String type=null;
		//营业执照
		if (items.toString().replaceAll(" ","").contains("营业执照")
				&& (items.toString().replaceAll(" ","").contains("法定代表人")
		   		|| items.toString().replaceAll(" ","").contains("经营场所"))
				&& (items.toString().replaceAll(" ","").contains("登记机关")
				|| items.toString().replaceAll(" ","").contains("登记"))) {
			type = "营业执照";
		}
		//银行回单
		if (items.toString().contains("回单")
				&& items.toString().contains("银行")
				&& items.toString().contains("开户行")) {
			type = "银行回单";
		}
		//支付宝支付凭证
		if ((items.toString().contains("账单详情")
				|| items.toString().contains("账单"))
				&& items.toString().contains("付款方式")
				&& items.toString().contains("订单号")
				&& items.toString().contains("创建时间")) {
			type = "支付宝支付凭证";
		}
		//微信支付凭证
		if (items.toString().contains("当前状态")
				&& items.toString().contains("支付方式")
				&& (items.toString().contains("支付时间")
		 		|| items.toString().contains("转账时间"))) {
			type = "微信支付凭证";
		}
		//网银支付凭证
		if (items.toString().contains("交易卡号")
				&& items.toString().contains("收款银行")
				&& items.toString().contains("收款账号")
				&& items.toString().contains("转账附言")
				&& items.toString().contains("银行摘要")
				&& items.toString().contains("银行交易")) {
			type = "网银支付凭证";
		}
		//银行回单
		if (items.toString().contains("回单")
				&& items.toString().contains("付款")
				&& items.toString().contains("收款")) {
			type = "银行回单";
		}
		//刷卡小票
		if (items.toString().contains("终端")
				&& items.toString().contains("商户")) {
			type = "刷卡小票";
		}
		//深圳区块链发票
		if ((items.toString().replaceAll(" ","").contains("深圳电子普通发票")
			||items.toString().replaceAll(" ","").contains("普通发票"))
			&&items.toString().replaceAll(" ","").contains("深圳")
			&&(!items.toString().replaceAll(" ","").contains("增值税"))){
			type = "blockchain_invoice";
		}
		//封面
		if (((items.toString().replaceAll(" ","").contains("基础信息")
			||items.toString().replaceAll(" ","").contains("基础"))
			&& (items.toString().replaceAll(" ","").contains("申请人信息")
			||items.toString().replaceAll(" ","").contains("申请人"))
			&& (items.toString().replaceAll(" ","").contains("费用报销信息")
			||items.toString().replaceAll(" ","").contains("费用报销"))
			) ||
			( items.toString().replaceAll(" ","").contains("流水号")
		    &&(items.toString().replaceAll(" ","").contains("责任人")
			|| items.toString().replaceAll(" ","").contains("部门"))
		    && (items.toString().replaceAll(" ","").contains("项目名称")
			|| items.toString().replaceAll(" ","").contains("项目"))
			&& items.toString().replaceAll(" ","").contains("业务单"))
			){
			type = "cover";
		}
		return type;
	}

	public  String ify(String base64String) {

		//发送文字识别请求
		String content = tableHttp(base64String);
		//封装list
		List<String> items = tableJsonParse(content);
		//表单匹配
		String type = TablesRule(items);
		if (type==null) {
			List<String> item = JsonParse(TextHttp(base64String));
			type=otherRule(item);
		}

		if (type != null){
			logger.warn("雅克康易方调用华为分类前处理识别得到类型");
		}
		return type;
	}

	/**
	 * 其他类型(other)票据的额外处理
	 * @param base64String
	 * @return
	 */
	public String other(String base64String){
		//发送文字识别请求
		String content = TextHttp(base64String);
		//封装list
		List<String> items = JsonParse(content);
		String type=null;
		String all = items.toString().replaceAll(" ", "");
		String replace = all.replaceAll(",", "");
		if (!items.isEmpty()){
			//客运车票
			if ((replace.contains("客运")
				|| replace.contains("运")
				|| replace.contains("客"))
				&& replace.contains("发票")
				&&(!replace.contains("船"))
				&&(replace.contains("乘车")
				|| replace.contains("乘")
				|| replace.contains("车"))){
				type="passenger_invoice";
			}else if (
			//船票
				(replace.contains("船票")
				|| replace.contains("船")
				|| replace.contains("轮船"))
				&&(replace.contains("发票")
				|| replace.contains("发")
				|| replace.contains("票"))
				&&(replace.contains("舱")
				|| replace.contains("航"))
			){
				type="ship_invoice";
			}
			if (
			//通用机打发票
				(replace.contains("通用机打发票")
				|| replace.contains("通用")
				|| replace.contains("机打"))
				&& (replace.contains("国家税务总局")
				|| replace.contains("国家税务"))
				&& replace.contains("开票"))
			{
				type="printed_invoice";
			}else {
				type="other";
			}
		}

		if (type != null){
			logger.warn("雅克康易方对华为分类后对other类处理识别得到类型");
		}
		return type;
	}


	/**
	 * 定额发票类型(quota)的其他处理
	 * @param base64String
	 * @return
	 */
	public String quota(String base64String){
		//发送文字识别请求
		String content = TextHttp(base64String);
		//封装list
		List<String> items = JsonParse(content);
		String type=null;
		if ((items.toString().replaceAll(" ","").contains("退票")
			|| items.toString().replaceAll(" ","").contains("退票费")))
		{
			type = "other";
		}else {
			type = "quota_invoice";
		}
		return  type;
	}


	/**
	 * 表格识别结果json解析
	 * @param content
	 * @return
	 */
	private  static  List<String> tableJsonParse(String content){
		JSONObject json=JSONObject.parseObject(content);
		JSONObject result = json.getJSONObject("result");
		JSONArray words_region_list = result.getJSONArray("words_region_list");
		List<String> items=new ArrayList<>();
		for (int i = 0; i < words_region_list.size(); i++) {
			JSONObject jsonObject = words_region_list.getJSONObject(i);
			if (jsonObject.getString("type").contains("table")){
				JSONArray words_block_list = jsonObject.getJSONArray("words_block_list");
				for (int j = 0; j < words_block_list.size(); j++) {
					JSONObject object = words_block_list.getJSONObject(j);
					String words = object.getString("words");
					items.add(words);
				}
			}
		}
		return  items;
	}

	/**
	 * 文字识别结果json解析
	 * @param content
	 * @return
	 */
	private  static  List<String> JsonParse(String content){
		JSONObject json=JSONObject.parseObject(content);
		JSONObject result = json.getJSONObject("result");
		JSONArray words_block_list = result.getJSONArray("words_block_list");
		List<String> items=new ArrayList<>();
		for (int i = 0; i < words_block_list.size(); i++) {
			JSONObject object = words_block_list.getJSONObject(i);
			String words = object.getString("words_result");
			items.add(words);
		}
		return  items;
	}


	/**
	 * 文字识别
	 * @param base64String  图片编码
	 * @return   文本识别
	 */
	private static final MediaType TYPE = MediaType.parse("application/json");
	private  String  TextHttp(String base64String){
		Response response;
		String content=null;
		try {
			if (base64String!=null){
				JSONObject json = new JSONObject();
				json.put("image", base64String);
				json.put("detect_direction",true);
				RequestBody body = RequestBody.create(TYPE, json.toJSONString());
				OkHttpClient httpClient = new OkHttpClient().newBuilder()
						.connectTimeout(2000, TimeUnit.SECONDS)
						.readTimeout(2000, TimeUnit.SECONDS).build();
				Request request = new Request.Builder()
						.url("http://"+cflProperties.getHost()+":"+cflProperties.getPort())
						.post(body)
						.build();
				response = httpClient.newCall(request).execute();
				content = response.body().string();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}


	@Autowired
	private TableProperties tableProperties;

	/**
	 * 发送通用表格识别请求
	 *
	 * @param base64string
	 * @return
	 */
	public String tableHttp(String base64string) {
		Response response = null;
		String content = null;
		try {
			JSONObject json = new JSONObject();
			json.put("image", base64string);
			RequestBody body = RequestBody.create(TYPE, json.toJSONString());
			OkHttpClient httpClient = new OkHttpClient().newBuilder()
					.connectTimeout(2000, TimeUnit.SECONDS)
					.readTimeout(2000, TimeUnit.SECONDS).build();
			Request request = new Request.Builder()
					.url("http://" + tableProperties.getHost() + ":" + tableProperties.getPort())
					.post(body)
					.build();
			response = httpClient.newCall(request).execute();
			content = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return content;
	}
}
