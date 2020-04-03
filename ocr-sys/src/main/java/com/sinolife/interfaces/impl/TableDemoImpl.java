package com.sinolife.interfaces.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinolife.interfaces.TableDemo;
import com.sinolife.properties.TableProperties;
import com.sinolife.util.ResultUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class TableDemoImpl implements TableDemo {

    @Autowired
    private TableProperties tableProperties;

    private Logger logger = LoggerFactory.getLogger(TableDemoImpl.class);


    /**
     * 工会经费计提表
     * @param base
     * @return
     */
    public  JSONObject tradeUnion(String base) {
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();
        String content = http(base);

        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        // content
        int intValue = -1;
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            // type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    // 机构名称
                    if (object.getString("words").equals("序号")) {
                        intValue = Integer.valueOf(object.getString("rows").replace("[", "").replace("]", ""))
                                .intValue();
                        intValue++;
                        int lastIndexOf = words_block_list.getJSONObject(j - 1).getString("words").lastIndexOf("：");
                        if (lastIndexOf != -1)
                            jsonResult.put("organization_name", words_block_list.getJSONObject(j - 1).getString("words")
                                    .substring((lastIndexOf + 1)));
                    }
                    // 合计
                    if (object.getString("words").equals("合计"))
                        jsonResult.put("total", words_block_list.getJSONObject(j + 1).getString("words"));
                    if (intValue != -1) {
                        for (int k = intValue; k < intValue + 12; k++) {
                            if (object.getString("rows").equals("[" + Integer.toString(k) + "]")
                                    && object.getString("columns").equals("[0,1]")) {
                                if (!"".equals(words_block_list.getJSONObject(j + 1).getString("words"))) {
                                    JSONObject o = new JSONObject(true);
                                    o.put("id", object.getString("words"));
                                    o.put("wage_channel", words_block_list.getJSONObject(j + 1).getString("words"));
                                    o.put("department", words_block_list.getJSONObject(j + 2).getString("words"));
                                    o.put("total_wages", words_block_list.getJSONObject(j + 3).getString("words"));
                                    o.put("count_table", words_block_list.getJSONObject(j + 4).getString("words"));
                                    o.put("company_total_wages", words_block_list.getJSONObject(j + 5).getString("words"));
                                    o.put("personal_part_amount", words_block_list.getJSONObject(j + 6).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                    if (object.getString("words").equals("户名")) {
                        boolean containsKey = maps
                                .containsKey(words_block_list.getJSONObject(j - 1).getString("words"));
                        if (!containsKey) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name", words_block_list.getJSONObject(j + 1).getString("words"));
                            map.put("amount", words_block_list.getJSONObject(j + 3).getString("words"));
                            map.put("price", words_block_list.getJSONObject(j + 5).getString("words"));
                            maps.put(words_block_list.getJSONObject(j - 1).getString("words"), map);
                        }
                    }
                    if (object.getString("words").contains("制表人")) {
                        jsonResult.put("create_table", !"".equals(words_block_list.getJSONObject(j + 1).getString("words")));
                        jsonResult.put("human_resources_department",!"".equals(words_block_list.getJSONObject(j + 3).getString("words")));
                    }
                }
            }
        }
        jsonResult.put("item", arr);
        JSONArray collection_information=new JSONArray();
        Iterator<String> iterator = maps.keySet().iterator();
        while (iterator.hasNext()) {
            JSONObject obj=new JSONObject(true);
            String next = iterator.next();
            Map<String, String> value = maps.get(next);
            obj.put(next, value);
            collection_information.add(obj);
        }
        jsonResult.put("collection_information", collection_information);
        return jsonResult;

    }


    /**
     * 评标汇总表
     * @param base
     * @return
     */
    public  JSONObject evaluationSummary(String base) {
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();
        JSONArray avg_list=new JSONArray();
        String content = http(base);

        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        int intValue = -1;
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            // type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    // 评标日期
                    if (object.getString("words").contains("评标日期")) {
                        jsonResult.put("bid_evaluation_data", words_block_list.getJSONObject(j + 1).getString("words"));
                    }
                    // find row
                    if (object.getString("words").equals("投标人5")) {
                        intValue = Integer.valueOf(object.getString("rows").replace("[", "").replace("]", ""))
                                .intValue();
                        intValue++;
                    }
                    if (intValue != -1) {
                        for (int k = intValue; k < intValue + 6; k++) {
                            if (object.getString("rows").equals("[" + Integer.toString(k) + "]")
                                    && object.getString("columns").equals("[0]")) {
                                if (!"".equals(words_block_list.getJSONObject(j + 1).getString("words"))) {
                                    JSONObject o = new JSONObject(true);
                                    o.put("id", object.getString("words"));
                                    o.put("name", words_block_list.getJSONObject(j + 1).getString("words"));
                                    o.put("department", words_block_list.getJSONObject(j + 2).getString("words"));
                                    o.put("bidder_1", words_block_list.getJSONObject(j + 3).getString("words"));
                                    o.put("bidder_2", words_block_list.getJSONObject(j + 4).getString("words"));
                                    o.put("bidder_3", words_block_list.getJSONObject(j + 5).getString("words"));
                                    o.put("bidder_4", words_block_list.getJSONObject(j + 6).getString("words"));
                                    o.put("bidder_5", words_block_list.getJSONObject(j + 7).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                    // 平均分值
                    if (object.getString("words").equals("平均分值")) {
                        JSONObject o = new JSONObject(true);
                        o.put("avg_bidder_1", words_block_list.getJSONObject(j + 1).getString("words"));
                        o.put("avg_bidder_2", words_block_list.getJSONObject(j + 2).getString("words"));
                        o.put("avg_bidder_3", words_block_list.getJSONObject(j + 3).getString("words"));
                        o.put("avg_bidder_4", words_block_list.getJSONObject(j + 4).getString("words"));
                        o.put("avg_bidder_5", words_block_list.getJSONObject(j + 5).getString("words"));
                        avg_list.add(o);
                    }
                    //评标人签字
                    if (object.getString("words").contains("评标人签字")) {
                        jsonResult.put("signature_of_bidder", !"".equals(words_block_list.getJSONObject(j + 1).getString("words")));
                    }
                }
            }
        }
        jsonResult.put("item", arr);
        jsonResult.put("avg_list", avg_list);
        return jsonResult;
    }


    /**
     * 方案达成明细表
     * @param base64String
     * @return
     */
    public JSONObject schemeReach(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        jsonObject.put("table_type","方案达成明细表");
        int a = 1;
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                if (object.getString("words").contains("制表人")) {
                    //制表人签名
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("部门负责人")) {
                    //部门负责人签名
                    jsonObject.put("department_head", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("财务经理")) {
                    //财务经理签名
                    jsonObject.put("financial_manager", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("分管总")) {
                    //分管总
                    jsonObject.put("general_manager", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("附件")){
                    a = 2;
                }
                for (int k = a; k < (a+23); k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("0")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //序号
                            job.put("numb",jsonArray.getJSONObject(j).getString("words"));
                            //机构
                            job.put("mechanism",jsonArray.getJSONObject(j + 1).getString("words"));
                            //达成人员工号
                            job.put("staff_numb",jsonArray.getJSONObject(j + 2).getString("words"));
                            //姓名
                            job.put("name",jsonArray.getJSONObject(j + 3).getString("words"));
                            //方案考核业绩
                            job.put("assessment_achievement",jsonArray.getJSONObject(j + 4).getString("words"));
                            //奖励内容
                            job.put("reward_content",jsonArray.getJSONObject(j + 5).getString("words"));
                            //奖项金额
                            job.put("award_amount",jsonArray.getJSONObject(j + 6).getString("words"));
                            //备注
                            job.put("remarks",jsonArray.getJSONObject(j + 7).getString("words"));
                            array.add(job);
                        }
                    }
                }
            }
        }
        jsonObject.put("item",array);
        return jsonObject;
    }


    /**
     * 费用分摊表
     * @param base64String
     * @return
     */
        public JSONObject costShare(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        jsonObject.put("table_type","费用分摊表");
        int a  = 3;
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                if (object.getString("words").contains("费用项目描述")){
                    jsonObject.put("cost_project",jsonArray.getJSONObject(j+1).getString("words"));
                }
                if (object.getString("words").contains("制表人")) {
                    //制表人签名
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("中心负责人")) {
                    //负责人签名
                    jsonObject.put("core_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("附件")){
                    a = 4;
                }

                for (int k = a; k < (a+12); k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("0")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //序号
                            job.put("numb",jsonArray.getJSONObject(j).getString("words"));
                            //预算中心
                            job.put("budget_core",jsonArray.getJSONObject(j + 1).getString("words"));
                            //成本中心
                            job.put("cost_core",jsonArray.getJSONObject(j + 2).getString("words"));
                            //预算科目
                            job.put("budget_subject",jsonArray.getJSONObject(j + 3).getString("words"));
                            //分摊金额
                            job.put("share_cost",jsonArray.getJSONObject(j + 4).getString("words"));
                            //分摊依据描述
                            job.put("share_basis",jsonArray.getJSONObject(j + 5).getString("words"));
                            array.add(job);
                        }
                    }
                }
                for (int k=0;k<jsonArray.size();k++){

                }


            }
        }

        jsonObject.put("item",array);
        return jsonObject;
    }



    /**
     * 邮寄费用明细清单
     * @param base64String
     * @return
     */
    public JSONObject mailCost(String base64String){
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        jsonObject.put("table_type","邮寄费用明细清单");
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                if (object.getString("words").contains("日期")) {
                    String words = jsonArray.getJSONObject(j - 1).getString("words");
                    String[] split = words.split("\\s");
                    if (split.length > 3) {
                        //公司
                        jsonObject.put("company", split[0]);
                        //部门
                        jsonObject.put("department", split[2]);
                    }
                }
                if (object.getString("words").contains("合计")) {
                    //邮寄费用合计
                    jsonObject.put("total",jsonArray.getJSONObject(j+1).getString("words"));
                }
                if (object.getString("words").contains("制表人")) {
                    //制表人签名
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("中支负责人")) {
                    //负责人签名
                    jsonObject.put("middle_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                for (int k = 3; k < 13; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j+1).getString("words"))) {
                            //日期
                            job.put("date",jsonArray.getJSONObject(j).getString("words"));
                            //邮寄物品
                            job.put("mail_goods",jsonArray.getJSONObject(j + 1).getString("words"));
                            //寄件地区
                            job.put("mailing_address",jsonArray.getJSONObject(j + 2).getString("words"));
                            //收件地区
                            job.put("receipt_address",jsonArray.getJSONObject(j + 3).getString("words"));
                            //邮寄费用
                            job.put("mail_cost",jsonArray.getJSONObject(j + 4).getString("words"));
                            //寄件人
                            job.put("name",jsonArray.getJSONObject(j + 5).getString("words"));
                            array.add(job);
                        }
                    }
                }
            }
        }
        jsonObject.put("item",array);
        return jsonObject;
    }


    /**
     * 个人所得税代扣代缴
     * @param
     * @return
     */
    public JSONObject individualInconmeTax(String type, String base) {
        JSONObject jsonObject = new JSONObject(true);
        if (type.contains("中支公司适用")){
            jsonObject = individualInconmeTax1(base);
        }else if (type.contains("总/分公司适用")){
            jsonObject = individualInconmeTax2(base);
        }
        return jsonObject;
    }

    /**
     * 个人所得税代扣代缴(中支公司适用)
     * @param base
     * @return
     */
    public  JSONObject individualInconmeTax1(String base)  {
        /**
         * 定义返回json
         */
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();

        //1.发送请求
        String content = http(base);

        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        //head
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").contains("中支") && object.getString("words").contains("分公司")&& object.getString("words").contains("部门")) {
                        int end = object.getString("words").indexOf("分");
                        if (end!=-1) {
                            //company
                            jsonResult.put("company", object.getString("words").substring(0,end).replace("_", "").trim());
                        }
                        int end2 = object.getString("words").lastIndexOf("中");
                        if (end2!=-1) {
                            //medium_counts
                            jsonResult.put("medium_counts",object.getString("words").substring(end+3,end2).replace("_", "").trim());
                        }

                        int end3 = object.getString("words").lastIndexOf("部");
                        if (end3!=-1) {
                            //department
                            jsonResult.put("department", object.getString("words").substring(end2+2,end3).replace("_", "").trim());
                        }
                    }
                }
            }
        }

        //找到起始行
        int startRow = 0;
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
            for (int j = 0; j < words_block_list.size(); j++) {
                JSONObject object = words_block_list.getJSONObject(j);
                if (object.getString("words").equals("姓名")) {
                    startRow=Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
                if (object.getString("words").equals("国籍")) {
                    startRow=Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
                if (object.getString("words").equals("税率")) {
                    startRow=Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
            }
        }


        // list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("rows")!=null) {
                        for (int k = startRow; k <startRow+16; k++) {
                            if (object.getString("rows").equals("["+k+"]")&& object.getString("columns").equals("[0]")) {
                                if(object.getString("words")!=null && !"".equals(object.getString("words"))) {
                                    JSONObject o=new JSONObject(true);
                                    o.put("name", object.getString("words"));
                                    o.put("nationality", words_block_list.getJSONObject(j+1).getString("words"));
                                    o.put("identity", words_block_list.getJSONObject(j+2).getString("words"));
                                    o.put("item_of_income", words_block_list.getJSONObject(j+3).getString("words"));
                                    o.put("invoice_reimbursement_amount", words_block_list.getJSONObject(j+4).getString("words"));
                                    o.put("taxable_income", words_block_list.getJSONObject(j+5).getString("words"));
                                    o.put("tax_rate", words_block_list.getJSONObject(j+6).getString("words"));
                                    o.put("tax_amount_payable", words_block_list.getJSONObject(j+7).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                }
            }
        }
        //添加集合
        jsonResult.put("item", arr);

        // 合計
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("合计")) {
                        //发票报销金额总计  invoice_reimbursement_amount
                        jsonResult.put("total_invoice_reimbursement_amount", words_block_list.getJSONObject(j+1).getString("words"));
                        //应得税所得额  taxable_income
                        jsonResult.put("total_taxable_income", words_block_list.getJSONObject(j+2).getString("words"));
                        //应纳税额  tax_amount_payable
                        jsonResult.put("total_tax_amount_payable", words_block_list.getJSONObject(j+4).getString("words"));
                    }
                }
            }
        }
        //签字
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    // 制表人
                    if (object.getString("words").contains("制表")) {
                        // 制表人
                        jsonResult.put("create_table", !"".equals(words_block_list.getJSONObject(j+1).getString("words")));
                        // 部门负责人
                        jsonResult.put("department_head", !"".equals(words_block_list.getJSONObject(j+3).getString("words")));
                        // 条线分总管
                        jsonResult.put("line_header", !"".equals(words_block_list.getJSONObject(j+5).getString("words")));
                        // 中支负责人  head of central branch
                        String words = words_block_list.getJSONObject(j+6).getString("words");
                        jsonResult.put("head_of_central_branch", !"".equals(words.substring(words.lastIndexOf("：")+1).trim()));
                    }
                }
            }
        }
        jsonResult.put("table_type", "个人所得税代扣代缴申请表(中支公司适用)");
        return jsonResult;
    }


    /**
     * 个人所得税代扣代缴(总分公司适用)
     * @param base
     * @return
     */
    public  JSONObject individualInconmeTax2(String base) {
        /**
         * 定义返回json
         */
        JSONObject jsonResult = new JSONObject(true);
        JSONArray arr = new JSONArray();
        // send http
        String content = http(base);

        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        //head
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            // type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("姓名")) {

                        int lastIndexOf = words_block_list.getJSONObject(j-1).getString("words").lastIndexOf("司");
                        if (lastIndexOf!=-1) {
                            //公司
                            jsonResult.put("company",words_block_list.getJSONObject(j-1).getString("words").substring(0, lastIndexOf).replace("公","").replace("_","").trim());
                            //部门
                            jsonResult.put("department",words_block_list.getJSONObject(j-1).getString("words").substring(lastIndexOf).replace("司","").replace("_","").replace(" ","").trim());
                        }else {
                            int lastIndexOf1 = words_block_list.getJSONObject(j-2).getString("words").lastIndexOf("司");
                            jsonResult.put("company",words_block_list.getJSONObject(j-2).getString("words").substring(0, lastIndexOf1).replace("公","").replace("_","").trim());
                            jsonResult.put("department",words_block_list.getJSONObject(j-2).getString("words").substring(lastIndexOf1).replace("司","").replace("_","").replace(" ","").trim());
                        }

//
                    }
                }
            }
        }
        // find startRow
        int startRow = 0;
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
            for (int j = 0; j < words_block_list.size(); j++) {
                JSONObject object = words_block_list.getJSONObject(j);
                if (object.getString("words").equals("姓名")) {
                    startRow = Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
                if (object.getString("words").equals("国籍")) {
                    startRow = Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
                if (object.getString("words").equals("税率")) {
                    startRow = Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
            }
        }

        // list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            // type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("rows") != null) {
                        for (int k = startRow; k < startRow + 16; k++) {
                            if (object.getString("rows").equals("[" + k + "]")
                                    && object.getString("columns").equals("[0]")) {
                                if (object.getString("words") != null && !"".equals(object.getString("words"))) {
                                    JSONObject o = new JSONObject(true);
                                    o.put("name", object.getString("words"));
                                    o.put("nationality", words_block_list.getJSONObject(j + 1).getString("words"));
                                    o.put("identity", words_block_list.getJSONObject(j + 2).getString("words"));
                                    o.put("item_of_income", words_block_list.getJSONObject(j + 3).getString("words"));
                                    o.put("invoice_reimbursement_amount",
                                            words_block_list.getJSONObject(j + 4).getString("words"));
                                    o.put("taxable_income", words_block_list.getJSONObject(j + 5).getString("words"));
                                    o.put("tax_rate", words_block_list.getJSONObject(j + 6).getString("words"));
                                    o.put("tax_amount_payable",
                                            words_block_list.getJSONObject(j + 7).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                }
            }
        }
        // 添加集合
        jsonResult.put("item", arr);

        // 合計
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            // type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("合计")) {
                        // 发票报销金额总计 invoice_reimbursement_amount
                        jsonResult.put("total_invoice_reimbursement_amount",
                                words_block_list.getJSONObject(j + 1).getString("words"));
                        // 应得税所得额 taxable_income
                        jsonResult.put("total_taxable_income",
                                words_block_list.getJSONObject(j + 2).getString("words"));
                        // 应纳税额 tax_amount_payable
                        jsonResult.put("total_tax_amount_payable",
                                words_block_list.getJSONObject(j + 4).getString("words"));
                    }
                    // 签字
                    // 製表人
                    if (object.getString("words").contains("制表人")) {
                        jsonResult.put("create_table", !"".equals(words_block_list.getJSONObject(j + 1).getString("words")));
                    }
                    //分公司部门负责人
                    if (object.getString("words").contains("分公司部门负责人")) {
                        jsonResult.put("department_head",!"".equals(words_block_list.getJSONObject(j + 1).getString("words")));
                    }
                }
            }
        }
        // 签字
        jsonResult.put("table_type", "个人所得税代扣代缴申请表(总分公司适用)");
        return jsonResult;
    }


    /**
     * 招待费用明细(总/分公司适用)
     * @author wenxiaolin
     */
    public  JSONObject entertainmentExpenses(String baseStr) {
        /**
         * 定义返回json
         */
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();

        //1.发送请求
        String content = http(baseStr);

        JSONObject jsonObject=JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        //找到起始行
        int startRow=0;
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
            for (int j = 0; j < words_block_list.size(); j++) {
                JSONObject object = words_block_list.getJSONObject(j);
                if (object.getString("words").equals("备注")) {
                    startRow=Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }if (object.getString("words").equals("金额")) {
                    startRow=Integer.parseInt(object.getString("rows").replace("[", "").replace("]", ""));
                    startRow++;
                    break;
                }
            }
        }
        //公司部门
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("rows").equals("[1]") && object.getString("words").contains("公司")) {
                        int end=object.getString("words").lastIndexOf("公司");
                        // 公司
                        jsonResult.put("company",object.getString("words").substring(0, end).replace("_", "").trim());
                        //部门
                        int end2=object.getString("words").lastIndexOf("部");
                        jsonResult.put("department", object.getString("words").substring(end+2, end2).trim());
                    }
                }
            }
        }

        // list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("rows")!=null) {
                        for (int k = startRow; k <14; k++) {
                            if (object.getString("rows").equals("["+k+"]")&& object.getString("columns").equals("[0]")) {
                                if(object.getString("words")!=null && !"".equals(object.getString("words"))) {
                                    JSONObject o=new JSONObject(true);
                                    o.put("id", object.getString("words"));
                                    o.put("reception_date", words_block_list.getJSONObject(j+1).getString("words"));
                                    o.put("reception_type", words_block_list.getJSONObject(j+2).getString("words"));
                                    o.put("reception_affair", words_block_list.getJSONObject(j+3).getString("words"));
                                    o.put("merchant_name", words_block_list.getJSONObject(j+4).getString("words"));
                                    o.put("best_job", words_block_list.getJSONObject(j+5).getString("words"));
                                    o.put("number_total", words_block_list.getJSONObject(j+6).getString("words"));
                                    o.put("price", words_block_list.getJSONObject(j+7).getString("words"));
                                    o.put("operator", words_block_list.getJSONObject(j+8).getString("words"));
                                    o.put("desc", words_block_list.getJSONObject(j+9).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                }
            }
        }
        jsonResult.put("item", arr);

        //合计
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("合 计")) {
                        //1.关键字
                        jsonResult.put("total", words_block_list.getJSONObject(j+4).getString("words"));
                        break;
                    }else if(object.getString("rows").equals("[14]") && object.getString("columns").equals("[7]")) {
                        //2.绝对位置
                        jsonResult.put("total", object.getString("words"));
                        break;
                    }
                }
            }
        }

        //签字
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").contains("制表")) {
                        //create_table
                        jsonResult.put("create_table", !"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                        //head_of_branch_department
                        jsonResult.put("head_of_branch_department", !"".equals(words_block_list.getJSONObject(j+3).getString("words").trim()));
                        //branch_branch_line_main
                        jsonResult.put("branch_branch_line_main", !"".equals(words_block_list.getJSONObject(j+5).getString("words").trim()));
                    }
                }
            }
        }
        jsonResult.put("table_type", "招待费用明细(总/分公司适用)");
        return jsonResult;
    }


    /**
     * 理赔查勘费用清单(分/支公司适用)
     * @author wenxiaolin
     */
    public JSONObject claimsSurvey(String base) {
        /**
         * 定义返回JSON
         */
        JSONObject jsonResult=new JSONObject(true);
        JSONArray item=new JSONArray();

        //1.文字识别
        String content = http(base);

        //2.JSON抽取
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        //公司部门
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").contains("中支") && object.getString("words").contains("分公司")&& object.getString("words").contains("部")) {
                        int end = object.getString("words").lastIndexOf("分公司");
                        // company 公司
                        jsonResult.put("company", object.getString("words").substring(0,end).replace("_", "").trim());
                        int end2 = object.getString("words").lastIndexOf("中支");
                        // medium_counts 支
                        jsonResult.put("medium_counts",object.getString("words").substring(end+3,end2).replace("_", "").trim());
                        int end3 = object.getString("words").lastIndexOf("部");
                        // department 部门
                        jsonResult.put("department", object.getString("words").substring(end2+2,end3).replace("_", "").trim());
                    }
                }
            }
        }

        //list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    for (int k = 1; k < 11; k++) {
                        if (object.getString("columns").equals("[0]") && object.getString("words").equals(Integer.toString(k)) ) {
                            if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                JSONObject o=new JSONObject(true);
                                //序号
                                o.put("id", object.getString("words"));
                                //票据序号
//                              o.put("note_the_serial_number", words_block_list.getJSONObject(j+1).getString("words"));
                                o.put("note_the_serial_number", words_block_list.getJSONObject(j+1).getString("words").replaceAll("\n|\r",""));
                                //日期
                                o.put("date", words_block_list.getJSONObject(j+2).getString("words"));
                                //事由
                                o.put("particulars_of_matter", words_block_list.getJSONObject(j+3).getString("words"));
                                //渠道
                                o.put("ditch", words_block_list.getJSONObject(j+4).getString("words"));
                                //投保单号
                                o.put("insurance_application_no", words_block_list.getJSONObject(j+5).getString("words"));
                                //查勘地点
                                o.put("survey _the_site", words_block_list.getJSONObject(j+6).getString("words"));
                                //市内交通费
                                o.put("business_traveling", words_block_list.getJSONObject(j+7).getString("words"));
                                //探视费
                                o.put("visiting_fee", words_block_list.getJSONObject(j+8).getString("words"));
                                //复印费
                                o.put("charge_for_copying_copy", words_block_list.getJSONObject(j+9).getString("words"));
                                //其他
                                o.put("else", words_block_list.getJSONObject(j+10).getString("words"));
                                //合计
                                o.put("total", words_block_list.getJSONObject(j+11).getString("words"));
                                //备注
                                o.put("remark", words_block_list.getJSONObject(j+12).getString("words"));
                                item.add(o);
                            }
                        }
                    }
                }
            }
        }
        //添加集合
        jsonResult.put("item", item);
        //合计
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("合计") && !object.getString("rows").equals("[3]")) {
                        //total_business_traveling 市内交通费合计
                        jsonResult.put("total_business_traveling", words_block_list.getJSONObject(j+1).getString("words"));
                        //total_visiting_fee 探视费合计
                        jsonResult.put("total_visiting_fee", words_block_list.getJSONObject(j+2).getString("words"));
                        //total_charge_for_copying_copy 复印费合计
                        jsonResult.put("total_charge_for_copying_copy", words_block_list.getJSONObject(j+3).getString("words"));
                        //total_else 其他合计
                        jsonResult.put("total_else", words_block_list.getJSONObject(j+4).getString("words"));
                        //total_total 合计总计
                        jsonResult.put("total_total", words_block_list.getJSONObject(j+5).getString("words"));
                    }
                }
            }
        }

        //签字
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").contains("制表")) {
                        //制表人
                        jsonResult.put("create_table", !"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                        //分/支公司营运部门负责人
                        jsonResult.put("operation_department ", !"".equals(words_block_list.getJSONObject(j+3).getString("words").trim()));
                    }
                }
            }
        }
        //加入单据类型
        jsonResult.put("table_type", "理赔查勘费用清单(分/支公司适用)");
        return jsonResult;
    }

    /**
     * 采购申请单
     *
     * @param
     * @return
     */
    @Override
    public JSONObject purchaseRequisition(String type, String base) {
        JSONObject jsonObject = new JSONObject(true);
        if (type.contains("中支公司适用")){
             jsonObject = purchaseRequisition2(base);
        }else if (type.contains("集团适用")){
            jsonObject = purchaseRequisition1(base);
        }else if (type.contains("总/分公司适用")){
            jsonObject = purchaseRequisition3(base);
        }
        return jsonObject;
    }


    /**
     * 采购申请单(总分公司适用)
     */
    public  JSONObject purchaseRequisition3(String base) {

        JSONObject jsonResult = new JSONObject(true);
        JSONArray arr = new JSONArray();
        JSONArray payInfo = new JSONArray();

        //发送请求
        String content = http(base);

        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        //head
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    //company
                    if (object.getString("words").contains("申请人姓名")) {
                        int lastIndexOf = words_block_list.getJSONObject(j-1).getString("words").lastIndexOf("公");
                        if (lastIndexOf!=-1) {
                            jsonResult.put("company", words_block_list.getJSONObject(j-1).getString("words").subSequence(0, lastIndexOf));
                        }
                        //申请人姓名
                        jsonResult.put("name_of_spplicant", words_block_list.getJSONObject(j+1).getString("words"));
                        //部门
                        jsonResult.put("department",words_block_list.getJSONObject(j+3).getString("words"));
                        //预算中心代码
                        jsonResult.put("code",words_block_list.getJSONObject(j+5).getString("words"));
                        //申请事项
                        jsonResult.put("items_of_application",words_block_list.getJSONObject(j+7).getString("words"));
                        //用途
                        jsonResult.put("user",words_block_list.getJSONObject(j+9).getString("words"));
                    }
                }
            }
        }

        //content
        int intValue=-1;
        int intValue2=-1;
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").equals("备注")) {
                        intValue = Integer.valueOf(object.getString("rows").replace("[", "").replace("]", "")).intValue();
                        intValue++;
                    }
                    if (intValue!=-1) {
                        for (int k = intValue; k < intValue+11; k++) {
                            if (object.getString("rows").equals("["+Integer.toString(k)+"]") && object.getString("columns").equals("[0,1]")) {
                                if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                    JSONObject o=new JSONObject(true);
                                    o.put("id", object.getString("words"));
                                    o.put("name",words_block_list.getJSONObject(j+1).getString("words"));
                                    o.put("specification",words_block_list.getJSONObject(j+2).getString("words"));
                                    o.put("unit",words_block_list.getJSONObject(j+3).getString("words"));
                                    o.put("count",words_block_list.getJSONObject(j+4).getString("words"));
                                    o.put("unit_price",words_block_list.getJSONObject(j+5).getString("words"));
                                    o.put("total",words_block_list.getJSONObject(j+6).getString("words"));
                                    o.put("remark",words_block_list.getJSONObject(j+7).getString("words"));
                                    arr.add(o);
                                }
                            }
                        }
                    }
                    // 合计
                    if (object.getString("words").contains("合") && object.getString("words").contains("元")) {
                        jsonResult.put("total", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                    // 预付款信息
                    if (object.getString("words").equals("金额")) {
                        intValue2 = Integer.valueOf(object.getString("rows").replace("[", "").replace("]", "")).intValue();
                        intValue2++;
                    }
                    if (intValue2!=-1) {
                        for (int l = intValue2; l < intValue2+2; l++) {
                            if (object.getString("rows").equals("["+Integer.toString(l)+"]") && object.getString("columns").contains("[0,1,2]")) {
                                if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                    JSONObject o=new JSONObject(true);
                                    o.put("username", object.getString("words"));
                                    o.put("amount",words_block_list.getJSONObject(j+1).getString("words"));
                                    o.put("price",words_block_list.getJSONObject(j+2).getString("words"));
                                    payInfo.add(o);
                                }
                            }
                        }
                    }
                }
            }
        }
        jsonResult.put("item", arr);
        jsonResult.put("payinfo", payInfo);

        //footer
        //签字
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    //申请部门负责人、预算部门负责人签字
                    if (object.getString("words").contains("申请部门负责人") || object.getString("words").contains("预算部门负责人")) {
                        jsonResult.put("申请部门负责人、预算部门负责人签字", !"".equals(words_block_list.getJSONObject(j+1).getString("words")));
                    }
                    //
                    if (object.getString("words").contains("分公司预算部门分管总") || object.getString("words").contains("分管总")) {
                        jsonResult.put("分公司预算部门分管总/总公司预算部门分管总签字", !"".equals(words_block_list.getJSONObject(j+1).getString("words")));
                    }
                    //总经理
                    if (object.getString("words").contains("总经理")) {
                        jsonResult.put("总经理", !"".equals(words_block_list.getJSONObject(j+1).getString("words")));
                    }
                }
            }
        }
        jsonResult.put("table_type", "采购申请单(总分公司适用)");
        return jsonResult;
    }

    /**
     * 采购申请单(集团适用)
     * @author wenxiaolin
     */
    private JSONObject purchaseRequisition1(String base) {
        /**
         * 定义返回json
         */
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();

        // 1.发送请求
        String content = http(base);


        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");

        // title
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    //中心
                    if ("申请人姓名".equals(object.getString("words"))) {
                        int lastIndexOf = words_block_list.getJSONObject(j-1).getString("words").lastIndexOf("中");
                        jsonResult.put("center", words_block_list.getJSONObject(j-1).getString("words").substring(0,lastIndexOf).replace("_", "").trim());
                    }
                    //申请人姓名
                    if ("申请人姓名".equals(object.getString("words"))) {
                        jsonResult.put("name_of_spplicant", words_block_list.getJSONObject(j+1).getString("words"));
                        jsonResult.put("department", words_block_list.getJSONObject(j+3).getString("words"));
                        jsonResult.put("budget_center_code", words_block_list.getJSONObject(j+5).getString("words"));
                    }
                    //申请事项
                    if ("申请事项".equals(object.getString("words"))) {
                        jsonResult.put("items_of_application", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                    //用途
                    if ("用途".equals(object.getString("words"))) {
                        jsonResult.put("use", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                }
            }
        }

        // list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    for (int k = 1; k < 11; k++) {
                        if (object.getString("words").equals(Integer.toString(k)) && object.getString("columns").equals("[0,1]")) {
                            if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                JSONObject o=new JSONObject(true);
                                o.put("id", object.getString("words"));
                                o.put("name",words_block_list.getJSONObject(j+1).getString("words"));
                                o.put("specification",words_block_list.getJSONObject(j+2).getString("words"));
                                o.put("unit",words_block_list.getJSONObject(j+3).getString("words"));
                                o.put("count",words_block_list.getJSONObject(j+4).getString("words"));
                                o.put("unit_price",words_block_list.getJSONObject(j+5).getString("words"));
                                o.put("total",words_block_list.getJSONObject(j+6).getString("words"));
                                o.put("remark",words_block_list.getJSONObject(j+7).getString("words"));
                                arr.add(o);
                            }

                        }
                    }
                }
            }
        }
        //添加到集合
        jsonResult.put("item", arr);

        JSONArray item2=new JSONArray();
        //预付款信息
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    if (object.getString("words").contains("合计")) {
                        int lastIndexOf = object.getString("words").lastIndexOf("元");
                        if (lastIndexOf!=-1) {
                            jsonResult.put("price", object.getString("words").substring(lastIndexOf+2));
                        }
                    }
                    for (int k = 1; k < 3; k++) {
                        if (object.getString("rows").equals("[2"+k+"]")&& object.getString("columns").equals("[0,1,2]")) {
                            if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                JSONObject  o=new JSONObject(true);
                                o.put("username", object.getString("words"));
                                o.put("account", words_block_list.getJSONObject(j+1).getString("words"));
                                o.put("price", words_block_list.getJSONObject(j+2).getString("words"));
                                item2.add(o);
                            }

                        }
                    }
                }
            }
        }
        ////添加到集合
        jsonResult.put("advance_info", item2);

        //签字
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    // 1
                    if ((object.getString("words").contains("申请部门负责人"))) {
                        jsonResult.put("申请部门负责人、预算部门负责人",!"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                    }
                    // 2
                    if ((object.getString("words").contains("中心负责人"))) {
                        jsonResult.put("中心负责人",!"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                    }
                    // 3
                    if ((object.getString("words").contains("中心分管总"))) {
                        jsonResult.put("中心分管总",!"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                    }
                    // 3
                    if ((object.getString("words").contains("人事行政中心"))) {
                        jsonResult.put("人事行政中心分管总",!"".equals(words_block_list.getJSONObject(j+1).getString("words").trim()));
                    }
                }
            }
        }
        //采购申请单(集团适用)
        jsonResult.put("table_type", "采购申请单(集团适用)");
        return jsonResult;
    }


    /**
     * 采购申请单(中支公司适用)
     * @author wenxiaolin
     */
    private JSONObject purchaseRequisition2(String base) {
        JSONObject jsonResult=new JSONObject(true);
        JSONArray arr=new JSONArray();
        // 1.发送请求
        String content = http(base);
        JSONObject jsonObject = JSONObject.parseObject(content);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray words_region_list = result.getJSONArray("words_region_list");
        jsonResult.put("table_type","采购申请单(中支公司适用)");
        //title
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")|tempObject.getString("type").equals("text")){
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    //公司
                    if (object.getString("words").equals("申请人姓名")&j!=0) {
                        if (!"".equals(words_block_list.getJSONObject(j-1).getString("words"))) {
                            int start = words_block_list.getJSONObject(j-1).getString("words").lastIndexOf("公司");
                            jsonResult.put("company", words_block_list.getJSONObject(j-1).getString("words").substring(0, start).replace("_", "").trim());
                        }
                    }
                    //申请人
                    if (object.getString("words").equals("申请人姓名")) {
                        jsonResult.put("name_of_spplicant", words_block_list.getJSONObject(j+1).getString("words"));
                        //所属部门
                        jsonResult.put("department", words_block_list.getJSONObject(j+3).getString("words"));
                        //预算中心代码
                        jsonResult.put("code",words_block_list.getJSONObject(j+5).getString("words"));
                    }
                    //申请事项
                    if (object.getString("words").equals("申请事项")) {
                        jsonResult.put("items_of_application", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                    //用途
                    if (object.getString("words").equals("用途")|object.getString("words").equals("申请理由")) {
                        jsonResult.put("use", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                }
            }
        }
        //list
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    for (int k = 7; k < 18; k++) {
                        if (object.getString("rows").equals("["+Integer.toString(k)+"]") && object.getString("columns").equals("[0,1]")) {
                            if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                JSONObject o=new JSONObject(true);
                                o.put("id", object.getString("words"));
                                o.put("name",words_block_list.getJSONObject(j+1).getString("words"));
                                o.put("specification",words_block_list.getJSONObject(j+2).getString("words"));
                                o.put("unit",words_block_list.getJSONObject(j+3).getString("words"));
                                o.put("count",words_block_list.getJSONObject(j+4).getString("words"));
                                o.put("unit_price",words_block_list.getJSONObject(j+5).getString("words"));
                                o.put("total",words_block_list.getJSONObject(j+6).getString("words"));
                                o.put("remark",words_block_list.getJSONObject(j+7).getString("words"));
                                arr.add(o);
                            }
                        }
                    }
                }
            }
        }
        //预付款信息
        for (int i = 0; i < words_region_list.size(); i++) {
            JSONObject tempObject = words_region_list.getJSONObject(i);
            //type=table
            if (tempObject.getString("type").equals("table")) {
                JSONArray words_block_list = tempObject.getJSONArray("words_block_list");
                for (int j = 0; j < words_block_list.size(); j++) {
                    JSONObject object = words_block_list.getJSONObject(j);
                    String rows = object.getString("rows");
                    String columns = object.getString("columns");
                    if (object.getString("words").contains("合计")) {
                        jsonResult.put("total", words_block_list.getJSONObject(j+1).getString("words"));
                    }
                    for (int k = 0; k < 3; k++) {
                        if (rows.equals("[2"+k+"]")&& object.getString("columns").equals("[0,1,2]")) {
                            if (!"".equals(words_block_list.getJSONObject(j+1).getString("words"))) {
                                JSONObject  o=new JSONObject(true);
                                o.put("username", object.getString("words"));
                                o.put("account", words_block_list.getJSONObject(j+1).getString("words"));
                                o.put("price", words_block_list.getJSONObject(j+2).getString("words"));
                                jsonResult.put("info",  o);
                            }
                        }
                    }
                    //签字
                    //申请部门负责人、预算\n部门负责人签字
                    if (rows.equals("[22]") && columns.equals("[3,4,5,6,7,8,9]")) {
                        jsonResult.put("申请部门负责人、预算部门负责人",object.getString("words"));
                    }
                    //中支预算部门\n分管总签字
                    if (rows.equals("[23]") && columns.equals("[3,4,5,6,7,8,9]")) {
                        jsonResult.put("中支预算部门分总管",object.getString("words"));
                    }
                    //中支负责人\n签字
                    if (rows.equals("[24]") && columns.equals("[0,1,2]")) {
                        jsonResult.put("中支负责人",object.getString("words"));
                    }
                }
            }
        }
        jsonResult.put("items", arr);
        return jsonResult;
    }

    /**
     * 会议培训签到表
     *
     * @param base64String
     * @return
     */
    public JSONObject meetingSignIn(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        jsonObject.put("table_type", "会议（培训）签到表");
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                //获取会议（培训）项目的名称
                if (object.getString("words").contains("会议（培训）名称")) {
                    jsonObject.put("meeting_name", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                //获取会议（培训）项目的地点
                if (object.getString("words").contains("会议（培训）地点")) {
                    jsonObject.put("meeting_address", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                //获取经办人的签名
                if (object.getString("words").contains("经办人")) {
                    jsonObject.put("responsible_person_signature", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                //获取部门负责人的签名
                if (object.getString("words").contains("部门负责人")) {
                    jsonObject.put("department_head_signature", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                for (int k = 4; k < 30; k++) {
                    if (object.getString("columns").contains("[2]") && object.getString("rows").contains("[" + k + "]")) {
                        if (object.getString("words") != null && !object.getString("words").equals("")) {
                            JSONObject job = new JSONObject();
                            job.put("conferee_name", jsonArray.getJSONObject(j - 1).getString("words"));
                            array.add(job);
                        }
                    }
                }
            }
        }
        //获取参会人员列表
        jsonObject.put("item", array);
        //获取参会人员到达个数
        jsonObject.put("conferee_numb", array.size());
        return jsonObject;
    }


    /**
     * 业务方案支出管理台账
     */
    public JSONObject yeWu(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        JSONObject job = new JSONObject(true);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                jsonObject.put("table_type", "业务方案支出管理台账");
                if (object.getString("words").contains("方案主题")) {
                    jsonObject.put("plan_theme", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("方案制定机构")) {
                    jsonObject.put("plan_custom_organization", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("制定业务渠道")) {
                    jsonObject.put("business_channel", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("方案编号")) {
                    jsonObject.put("plan_numb", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("内请编号")) {
                    jsonObject.put("information_numb", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("制表人") || object.getString("words").contains("表人")) {
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("部门负责人") || object.getString("words").contains("负责人")) {
                    jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("财务部负责人") || object.getString("words").contains("财务部")) {
                    jsonObject.put("finance_department_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("分管领导")) {
                    jsonObject.put("general_leader_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }

                if (object.getString("words").contains("子方案项目")) {
                    job.put("sub_scheme_project_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("sub_scheme_project_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("sub_scheme_project_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("sub_scheme_project_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("sub_scheme_project_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("考核期间")) {
                    job.put("assessment_period_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("assessment_period_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("assessment_period_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("assessment_period_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("assessment_period_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("考核对象")) {
                    job.put("assessment_target_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("assessment_target_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("assessment_target_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("assessment_target_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("assessment_target_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("达成条件")) {
                    job.put("conditions_reached_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("conditions_reached_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("conditions_reached_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("conditions_reached_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("conditions_reached_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("奖励形式")) {
                    job.put("reward_form_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("reward_form_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("reward_form_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("reward_form_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("reward_form_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("奖励内容")) {
                    job.put("reward_content_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("reward_content_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("reward_content_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("reward_content_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("reward_content_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("预计达成数量")) {
                    job.put("budget_numb_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("budget_numb_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("budget_numb_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("budget_numb_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("budget_numb_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("预算单价")) {
                    job.put("budget_univalence_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("budget_univalence_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("budget_univalence_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("budget_univalence_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("budget_univalence_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("实际达成数量")) {
                    job.put("actual_reach_numb_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("actual_reach_numb_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("actual_reach_numb_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("actual_reach_numb_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("actual_reach_numb_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("实际兑现数量")) {
                    job.put("actual_cash_numb_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("actual_cash_numb_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("actual_cash_numb_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("actual_cash_numb_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("actual_cash_numb_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("结算单价")) {
                    job.put("settlement_univalence_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("settlement_univalence_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("settlement_univalence_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("settlement_univalence_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("settlement_univalence_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("结算总额")) {
                    job.put("settlement_total_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("settlement_total_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("settlement_total_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("settlement_total_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("settlement_total_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("申请单号")) {
                    job.put("apply_numb_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("apply_numb_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("apply_numb_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("apply_numb_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("apply_numb_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("申请金额")) {
                    job.put("apply_money_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("apply_money_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("apply_money_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("apply_money_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("apply_money_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("报销单号")) {
                    job.put("reimbursement_numb_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("reimbursement_numb_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("reimbursement_numb_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("reimbursement_numb_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("reimbursement_numb_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("报销金额")) {
                    job.put("reimbursement_money_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("reimbursement_money_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("reimbursement_money_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("reimbursement_money_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("reimbursement_money_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("合并计税时间")) {
                    job.put("merge_taxation_time_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("merge_taxation_time_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("merge_taxation_time_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("merge_taxation_time_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("merge_taxation_time_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("合并计税金额")) {
                    job.put("merge_taxation_money_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("merge_taxation_money_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("merge_taxation_money_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("merge_taxation_money_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("merge_taxation_money_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
                if (object.getString("words").contains("备注")) {
                    job.put("remarks_1", jsonArray.getJSONObject(j + 1).getString("words"));
                    job.put("remarks_2", jsonArray.getJSONObject(j + 2).getString("words"));
                    job.put("remarks_3", jsonArray.getJSONObject(j + 3).getString("words"));
                    job.put("remarks_4", jsonArray.getJSONObject(j + 4).getString("words"));
                    job.put("remarks_5", jsonArray.getJSONObject(j + 5).getString("words"));
                }
            }
        }

        JSONObject obj1 = new JSONObject(true);
        JSONObject obj2 = new JSONObject(true);
        JSONObject obj3 = new JSONObject(true);
        JSONObject obj4 = new JSONObject(true);
        JSONObject obj5 = new JSONObject(true);
        Map<String, Object> map = new HashMap<>(job);
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (key.endsWith("1")) {
                obj1.put(key, map.get(key));
            }
            if (key.endsWith("2")) {
                obj2.put(key, map.get(key));
            }
            if (key.endsWith("3")) {
                obj3.put(key, map.get(key));
            }
            if (key.endsWith("4")) {
                obj4.put(key, map.get(key));
            }
            if (key.endsWith("5")) {
                obj5.put(key, map.get(key));
            }
        }
        array.add(obj1);
        array.add(obj2);
        array.add(obj3);
        array.add(obj4);
        array.add(obj5);

        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 手续费汇总计算表
     */
    public JSONObject shouXuFei(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        int a = 3;
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                if (object.getString("words").contains("附件")){
                    a = 4;
                }
                for (int k = a; k < (a+13); k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //序号
                            job.put("order_numb", jsonArray.getJSONObject(j).getString("words"));
                            //机构
                            job.put("mechanism", jsonArray.getJSONObject(j + 1).getString("words"));
                            //销售渠道
                            job.put("sale_channel", jsonArray.getJSONObject(j + 2).getString("words"));
                            //险种全称
                            job.put("insurance_name", jsonArray.getJSONObject(j + 3).getString("words"));
                            //计税属性
                            job.put("taxation_attribute", jsonArray.getJSONObject(j + 4).getString("words"));
                            //缴费年期
                            job.put("pay_period", jsonArray.getJSONObject(j + 5).getString("words"));
                            //保单保费
                            job.put("insurance_policy_cost", jsonArray.getJSONObject(j + 6).getString("words"));
                            //手续费率
                            job.put("service_charge_rate", jsonArray.getJSONObject(j + 7).getString("words"));
                            //手续费金额
                            job.put("service_charge_money", jsonArray.getJSONObject(j + 8).getString("words"));
                            //出单费标准
                            job.put("issue_money_standard", jsonArray.getJSONObject(j + 9).getString("words"));
                            //出单费单量
                            job.put("issue_single_volume", jsonArray.getJSONObject(j + 10).getString("words"));
                            //出单费金额
                            job.put("issue_money", jsonArray.getJSONObject(j + 11).getString("words"));
                            //保全业务费标准
                            job.put("preservation_money_standard", jsonArray.getJSONObject(j + 12).getString("words"));
                            //保全业务费单量
                            job.put("preservation_single_volume", jsonArray.getJSONObject(j + 13).getString("words"));
                            //保全业务费金额
                            job.put("preservation_money", jsonArray.getJSONObject(j + 14).getString("words"));
                            //合计
                            job.put("total", jsonArray.getJSONObject(j + 15).getString("words"));
                            array.add(job);
                        }
                    }
                }
                jsonObject.put("table_type", "手续费汇总计算表");
                if (object.getString("words").contains("期间") && object.getString("columns").contains("[0]")) {
                    jsonObject.put("period", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("渠道")) {
                    jsonObject.put("channel", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("rows").contains("17") && object.getString("columns").contains("12")) {
                    //户名1
                    jsonObject.put("account_name_01", jsonArray.getJSONObject(j + 1).getString("words"));
                    //账号1
                    jsonObject.put("account_numb_01", jsonArray.getJSONObject(j + 2).getString("words"));
                    //户名2
                    jsonObject.put("account_name_02", jsonArray.getJSONObject(j + 3).getString("words"));
                    //账号2
                    jsonObject.put("account_numb_02", jsonArray.getJSONObject(j + 4).getString("words"));
                }
                if (object.getString("words").contains("制表人") || object.getString("words").contains("表人")) {
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("部门负责人") || object.getString("words").contains("负责人")) {
                    jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("财务经理") || object.getString("words").contains("经理")) {
                    jsonObject.put("finance_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("分管总")) {
                    jsonObject.put("general_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
            }
        }

        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 差旅费用明细表
     */
    public JSONObject chaiLv(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                for (int k = 3; k < 9; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //出差人员
                            job.put("name", jsonArray.getJSONObject(j).getString("words"));
                            //性别
                            job.put("sex", jsonArray.getJSONObject(j + 1).getString("words"));
                            //职位
                            job.put("post", jsonArray.getJSONObject(j + 2).getString("words"));
                            //出发地
                            job.put("depart_address", jsonArray.getJSONObject(j + 3).getString("words"));
                            //出差地
                            job.put("purpose_address", jsonArray.getJSONObject(j + 4).getString("words"));
                            //出差事由
                            job.put("cause", jsonArray.getJSONObject(j + 5).getString("words"));
                            //开始日期
                            job.put("start_date", jsonArray.getJSONObject(j + 6).getString("words"));
                            //结束日期
                            job.put("end_date", jsonArray.getJSONObject(j + 7).getString("words"));
                            //出差天数
                            job.put("days", jsonArray.getJSONObject(j + 8).getString("words"));
                            array.add(job);
                        }
                    }
                }

                //表单类型
                jsonObject.put("table_type", "差旅费用明细表(总/分公司适用)");

                if (object.getString("words").contains("出差人员")){
                    String words = jsonArray.getJSONObject(j - 5).getString("words");
                    String[] split = words.split("\\s");
                    jsonObject.put("company",split[0]);
                    jsonObject.put("department",split[2]);
                }

                if (object.getString("words").contains("金额")) {
                    //长途交通费金额
                    jsonObject.put("long_traffic_cost", jsonArray.getJSONObject(j + 1).getString("words"));
                    //市内交通费金额
                    jsonObject.put("city_traffic_cost", jsonArray.getJSONObject(j + 2).getString("words"));
                    //误餐费金额
                    jsonObject.put("meal_allowance_cost", jsonArray.getJSONObject(j + 3).getString("words"));
                    //住宿费金额
                    jsonObject.put("hotel_cost", jsonArray.getJSONObject(j + 4).getString("words"));
                    //订票/退票金额
                    jsonObject.put("booking_refund_cost", jsonArray.getJSONObject(j + 5).getString("words"));
                    //行李费金额
                    jsonObject.put("baggage_cost", jsonArray.getJSONObject(j + 6).getString("words"));
                    //培训费金额
                    jsonObject.put("train_cost", jsonArray.getJSONObject(j + 7).getString("words"));
                    //小计金额
                    jsonObject.put("total_cost", jsonArray.getJSONObject(j + 8).getString("words"));
                }
                if (object.getString("words").contains("票据序号")) {
                    //长途交通费票据号
                    jsonObject.put("long_traffic_bill", jsonArray.getJSONObject(j + 1).getString("words"));
                    //市内交通费票据号
                    jsonObject.put("city_traffic_bill", jsonArray.getJSONObject(j + 2).getString("words"));
                    //误餐费票据号
                    jsonObject.put("meal_allowance_bill", jsonArray.getJSONObject(j + 3).getString("words"));
                    //住宿费票据号
                    jsonObject.put("hotel_bill", jsonArray.getJSONObject(j + 4).getString("words"));
                    //订票/退票票据号
                    jsonObject.put("booking_refund_bill", jsonArray.getJSONObject(j + 5).getString("words"));
                    //行李费票据号
                    jsonObject.put("baggage_bill", jsonArray.getJSONObject(j + 6).getString("words"));
                    //培训费票据号
                    jsonObject.put("train_bill", jsonArray.getJSONObject(j + 7).getString("words"));
                    //合计票据号
                    jsonObject.put("total_bill", jsonArray.getJSONObject(j + 8).getString("words"));
                }
                if (object.getString("words").contains("制表人")) {
                    //制表人签名
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("分公司部门负责人")) {
                    //分公司部门负责人签名
                    jsonObject.put("department_head_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("分管总")) {
                    //分公司分管总签名
                    jsonObject.put("general_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
            }
        }
        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 车辆费用报销清单
     */
    public JSONObject carCost(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                for (int k = 4; k < 14; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //获取票据序号
                            job.put("ticket_numb", jsonArray.getJSONObject(j).getString("words"));
                            //获取日期
                            job.put("date", jsonArray.getJSONObject(j + 1).getString("words"));
                            //获取事由
                            job.put("cause", jsonArray.getJSONObject(j + 2).getString("words"));
                            //获取出发地
                            job.put("depart_address", jsonArray.getJSONObject(j + 3).getString("words"));
                            //获取目的地
                            job.put("purpose_address", jsonArray.getJSONObject(j + 4).getString("words"));
                            //获取预计里程
                            job.put("predict_mileage", jsonArray.getJSONObject(j + 5).getString("words"));
                            //获取油价
                            job.put("oil_price", jsonArray.getJSONObject(j + 6).getString("words"));
                            //获取预计油费
                            job.put("predict_oil_cost", jsonArray.getJSONObject(j + 7).getString("words"));
                            //获取实际报销油费
                            job.put("practical_oil_cost", jsonArray.getJSONObject(j + 8).getString("words"));
                            //获取过路费
                            job.put("road_toll", jsonArray.getJSONObject(j + 9).getString("words"));
                            //获取停车费
                            job.put("parking_fee", jsonArray.getJSONObject(j + 10).getString("words"));
                            //获取洗车费
                            job.put("car_washing_fee", jsonArray.getJSONObject(j + 11).getString("words"));
                            //获取汽车维修费
                            job.put("car_maintain", jsonArray.getJSONObject(j + 12).getString("words"));
                            //获取汽车保养费
                            job.put("car_upkeep", jsonArray.getJSONObject(j + 13).getString("words"));
                            //获取合计金额
                            job.put("total", jsonArray.getJSONObject(j + 14).getString("words"));
                            array.add(job);
                        }
                    }
                }
                if (object.getString("rows").contains("[14]") && object.getString("words").contains("合计")) {
                    //获取预计总路程
                    jsonObject.put("all_predict_mileage", jsonArray.getJSONObject(j + 1).getString("words"));
                    //获取油费
                    jsonObject.put("all_oil_price", jsonArray.getJSONObject(j + 2).getString("words"));
                    //获取总预计油费
                    jsonObject.put("all_predict_oil_cost", jsonArray.getJSONObject(j + 3).getString("words"));
                    //获取总实际油费
                    jsonObject.put("all_practical_oil_cost", jsonArray.getJSONObject(j + 4).getString("words"));
                    //获取总过路费
                    jsonObject.put("all_road_toll", jsonArray.getJSONObject(j + 5).getString("words"));
                    //获取总停车费
                    jsonObject.put("all_parking_fee", jsonArray.getJSONObject(j + 6).getString("words"));
                    //获取总洗车费
                    jsonObject.put("all_car_washing_fee", jsonArray.getJSONObject(j + 7).getString("words"));
                    //获取总维修费
                    jsonObject.put("all_car_maintain", jsonArray.getJSONObject(j + 8).getString("words"));
                    //获取总保养费
                    jsonObject.put("all_car_upkeep", jsonArray.getJSONObject(j + 9).getString("words"));
                    //获取总合计金额
                    jsonObject.put("all_total", jsonArray.getJSONObject(j + 10).getString("words"));
                }
                if (object.getString("words").contains("辆类别")) {
                    //获取分公司以及部门信息
                    String[] strings = jsonArray.getJSONObject(j - 1).getString("words").split("\\s");
                    if (strings.length > 1) {
                        //获取分公司信息
                        jsonObject.put("company", strings[0]);
                        //获取部门信息
                        jsonObject.put("department", strings[1]);
                    } else {
                        jsonObject.put("corporateInformation", jsonArray.getJSONObject(j - 1).getString("words"));
                    }
                    //获取车辆类别
                    jsonObject.put("car_type", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("辆牌号")) {
                    //获取车牌号
                    jsonObject.put("car_numb", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("中支公司适用")) {
                    jsonObject.put("table_type", "车辆费用报销清单(中支公司适用)");
                }
                if (object.getString("words").contains("总/分公司适用")) {
                    jsonObject.put("table_type", "车辆费用报销清单(总/分公司适用)");
                }
                if (jsonObject.getString("table_type") != null) {
                    if (jsonObject.getString("table_type").contains("中支公司适用")) {
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("部门负责人")) {
                            //中支部门负责人签名
                            jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("中支负责人")) {
                            //中支负责人签名
                            jsonObject.put("middle_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                    }
                    if (jsonObject.getString("table_type").contains("总/分公司适用")) {
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司部门负责人")) {
                            //分公司部门负责人签名
                            jsonObject.put("department_head_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分管总")) {
                            //分公司分管总签名
                            jsonObject.put("general_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                    }
                }
            }
        }

        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 会议（培训）费用结算明细表
     */
    public JSONObject huiYiFeiYong(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        int content_count=0;
        int addr_count =0;
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            if (jsonArray.size() > 5) {
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject object = jsonArray.getJSONObject(j);
                    for (int k = 4; k < 23; k++) {
                        if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("0")) {
                            JSONObject job = new JSONObject(true);
                            if (!"".equals(jsonArray.getJSONObject(j+1).getString("words"))) {
                                //费用项目
                                job.put("cost_item", jsonArray.getJSONObject(j).getString("words"));
                                //报销金额
                                job.put("reimbursement_price", jsonArray.getJSONObject(j + 1).getString("words").replaceAll("\r|\n",""));
//                                job.put("reimbursement_price", jsonArray.getJSONObject(j + 1).getString("words"));
                                //费用明细
                                job.put("cost_detailed", jsonArray.getJSONObject(j + 2).getString("words"));
                                //票据序号
                                job.put("bill_numb", jsonArray.getJSONObject(j + 3).getString("words"));
                                //备注
                                job.put("remarks", jsonArray.getJSONObject(j + 4).getString("words"));
                                array.add(job);
                            }
                        }
                        if (object.getString("words").contains("中支公司适用")) {
                            jsonObject.put("table_type", "会议（培训）费用结算明细表(中支公司适用)");
                        }
                        if (object.getString("words").contains("总/分公司适用")) {
                            jsonObject.put("table_type", "会议（培训）费用结算明细表(总/分公司适用)");
                        }
                        if (object.getString("words").contains("培训内容") && object.getString("words").length()<10) {
                            //当培训内容后面的值为""时
                            content_count =1;
                            break;
                        }
                        if (content_count ==1){
                            jsonObject.put("train_content", object.getString("words"));
                            content_count=0;
                        }
                        if (object.getString("words").contains("培训内容") && object.getString("words").length()>10) {
                            //培训内容
                            jsonObject.put("train_content", object.getString("words").substring((object.getString("words").indexOf("："))).replace("：",""));
                        }
                        if (object.getString("words").contains("培训时间")) {
                            //培训时间
                            jsonObject.put("train_date", object.getString("words").substring((object.getString("words").indexOf("："))).replace("：",""));
                        }
                        if (object.getString("words").contains("培训地点") && object.getString("words").length()>10) {
                            //培训地点
                            jsonObject.put("train_address", object.getString("words").substring((object.getString("words").indexOf("："))).replace("：",""));

                        }
                        if (object.getString("words").contains("培训地点") && object.getString("words").length()<10){
                            addr_count = 1;
                            break;
                        }
                        if (addr_count ==1){
                            jsonObject.put("train_address",object.getString("words"));
                            addr_count =0;
                        }


                        if (object.getString("words").contains("培训人数")) {
                            //培训人数
                            jsonObject.put("train_numb", object.getString("words").substring((object.getString("words").indexOf("："))).replace("：",""));
                        }
                        if (object.getString("words").contains("合计")) {
                            //合计
                            jsonObject.put("total", jsonArray.getJSONObject(j + 1).getString("words"));
                        }
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司部门负责人")) {
                            //分公司部门负责人签名
                            jsonObject.put("department_head_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司分管总")) {
                            //分公司分管总签名
                            jsonObject.put("general_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                    }
                }
            }
        }
        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 外勤差旅费用明细表
     */
    public JSONObject waiQingChaiLv(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                for (int k = 6; k < 13; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //第一位序号
                            job.put("first_numb", jsonArray.getJSONObject(j).getString("words"));
                            //第一位姓名
                            job.put("first_name", jsonArray.getJSONObject(j + 1).getString("words"));
                            //第一位性别
                            job.put("first_sex", jsonArray.getJSONObject(j + 2).getString("words"));
                            //第一位部门
                            job.put("first_department", jsonArray.getJSONObject(j + 3).getString("words"));
                            //第一位出发地
                            job.put("first_address", jsonArray.getJSONObject(j + 4).getString("words"));
                        }
                        if (!"".equals(jsonArray.getJSONObject(j + 6).getString("words"))) {
                            //第二位序号
                            job.put("second_numb", jsonArray.getJSONObject(j + 5).getString("words"));
                            //第二位姓名
                            job.put("second_name", jsonArray.getJSONObject(j + 6).getString("words"));
                            //第二位性别
                            job.put("second_sex", jsonArray.getJSONObject(j + 7).getString("words"));
                            //第二位部门
                            job.put("second_department", jsonArray.getJSONObject(j + 8).getString("words"));
                            //第二位出发地
                            job.put("second_address", jsonArray.getJSONObject(j + 9).getString("words"));
                        }
                        array.add(job);
                    }
                }
                if (object.getString("words").contains("出差事由")) {
                    if (object.getString("words").endsWith("：")) {
                        //出差事由
                        jsonObject.put("travel_cause", jsonArray.getJSONObject(j + 1).getString("words"));
                    } else {
                        //出差事由
                        jsonObject.put("travel_cause", object.getString("words").substring(5));
                    }
                }
                if (object.getString("words").contains("目的地")) {
                    //出差目的地
                    jsonObject.put("travel_destination", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("业务开始日期")) {
                    //出差业务开始日期
                    jsonObject.put("start_date", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("业务结束日期")) {
                    //出差业务结束日期
                    jsonObject.put("end_date", jsonArray.getJSONObject(j + 1).getString("words"));
                }
                if (object.getString("words").contains("汇总金额")) {
                    //长途交通费
                    jsonObject.put("long_traffic_cost", jsonArray.getJSONObject(j + 1).getString("words"));
                    //市内交通费
                    jsonObject.put("city_traffic_cost", jsonArray.getJSONObject(j + 2).getString("words"));
                    //住宿费
                    jsonObject.put("hotel_cost", jsonArray.getJSONObject(j + 3).getString("words"));
                    //行李费
                    jsonObject.put("baggage_cost", jsonArray.getJSONObject(j + 4).getString("words"));
                }
                if (object.getString("words").contains("分公司适用")) {
                    //表单类型
                    jsonObject.put("table_type", "外勤差旅费用明细表(分公司适用)");
                }
                if (object.getString("words").contains("中支公司适用")) {
                    //表单类型
                    jsonObject.put("table_type", "外勤差旅费用明细表(中支公司适用)");
                }
                if (jsonObject.getString("table_type") != null) {
                    if (jsonObject.getString("table_type").contains("分公司适用")) {
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司部门负责人")) {
                            //分公司部门负责人签名
                            jsonObject.put("department_head_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司分管总")) {
                            //分公司分管总签名
                            jsonObject.put("general_manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                    }
                    if (jsonObject.getString("table_type").contains("中支公司适用")) {
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("部门负责人")) {
                            //部门负责人签名
                            jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("中支负责人")) {
                            //中支负责人签名
                            jsonObject.put("middle_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                    }
                }
            }
        }
        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 交通费及误餐费报销清单
     */
    public JSONObject jiaoTongWuCanFei(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                for (int k = 3; k < 14; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("[0]")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //票据序号
                            job.put("bill_numb", jsonArray.getJSONObject(j).getString("words"));
                            //经办人
                            job.put("operator", jsonArray.getJSONObject(j + 1).getString("words"));
                            //日期
                            job.put("date", jsonArray.getJSONObject(j + 2).getString("words"));
                            //星期
                            job.put("week", jsonArray.getJSONObject(j + 3).getString("words"));
                            //事由
                            job.put("cause", jsonArray.getJSONObject(j + 4).getString("words"));
                            //上车地点
                            job.put("get_on_address", jsonArray.getJSONObject(j + 5).getString("words"));
                            //下车地点
                            job.put("get_off_address", jsonArray.getJSONObject(j + 6).getString("words"));
                            //上车时间
                            job.put("get_on_time", jsonArray.getJSONObject(j + 7).getString("words"));
                            //下车时间
                            job.put("get_off_time", jsonArray.getJSONObject(j + 8).getString("words"));
                            //交通费
                            job.put("traffic_expense", jsonArray.getJSONObject(j + 9).getString("words"));
                            //误餐费
                            job.put("meal_allowance", jsonArray.getJSONObject(j + 10).getString("words"));
                            //合计
                            job.put("total", jsonArray.getJSONObject(j + 11).getString("words"));
                            array.add(job);
                        }
                    }
                }
                if (object.getString("words").contains("合计") && object.getString("rows").contains("[14]")) {
                    //总计交通费
                    jsonObject.put("all_traffic_expense", jsonArray.getJSONObject(j + 1).getString("words"));
                    //总计误餐费
                    jsonObject.put("all_meal_allowance", jsonArray.getJSONObject(j + 2).getString("words"));
                    //总计合计
                    jsonObject.put("all_total", jsonArray.getJSONObject(j + 3).getString("words"));
                }
                if (object.getString("words").contains("总/分公司适用")) {
                    jsonObject.put("table_type", "交通费及误餐费报销清单(总/分公司适用)");
                }
                if (object.getString("words").contains("中支公司适用")) {
                    jsonObject.put("table_type", "交通费及误餐费报销清单(中支公司适用)");
                }
                if (jsonObject.getString("table_type") != null) {
                    if (jsonObject.getString("table_type").contains("总/分公司适用")) {
                        if (object.getString("rows").contains("[1]")) {
                            String[] split = object.getString("words").split("\\s+");
                            //公司
                            jsonObject.put("company", (split[0] + split[1]));
                            //部门
                            jsonObject.put("department", (split[2] + split[3]));
                        }
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("分公司部门负责人")) {
                            //分公司部门负责人签名
                            jsonObject.put("manager_autograph", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));

                        }
                    }
                    if (jsonObject.getString("table_type").contains("中支公司适用")) {
                        if (object.getString("rows").contains("[1]")) {
                            String[] split = object.getString("words").replace("_","").split("\\s+");
                            if (split.length < 6) {
                                //分公司
                                jsonObject.put("branch_office", (split[0] + split[1]));
                                //中支
                                jsonObject.put("middle_branch", split[2]);
                                //部门
                                jsonObject.put("department", (split[3] + split[4]));
                            } else {
                                //分公司
                                jsonObject.put("branch_office", (split[0] + split[1]));
                                //中支
                                jsonObject.put("middle_branch", (split[2] + split[3]));
                                //部门
                                jsonObject.put("department", (split[4] + split[5]));
                            }
                        }
                        if (object.getString("words").contains("制表人")) {
                            //制表人签名
                            jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("部门负责人")) {
                            //营运部门负责人是否签名
                            jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                        }
                        if (object.getString("words").contains("中支负责人")) {
                            //中支付责人签名
                            jsonObject.put("middle_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));

                        }
                    }
                }
            }
        }
        jsonObject.put("item", array);
        return jsonObject;
    }


    /**
     * 体检费用清单识别
     */
    public JSONObject checkUpCost(String base64String) {
        JSONObject jsonObject = new JSONObject(true);
        JSONArray array = new JSONArray();
        String content = http(base64String);
        JSONArray tables = ResultUtils.GetTables(content);
        for (int i = 0; i < tables.size(); i++) {
            JSONArray jsonArray = tables.getJSONArray(i);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject object = jsonArray.getJSONObject(j);
                for (int k = 2; k < 14; k++) {
                    if (object.getString("rows").contains("[" + k + "]") && object.getString("columns").contains("0")) {
                        JSONObject job = new JSONObject(true);
                        if (!"".equals(jsonArray.getJSONObject(j + 1).getString("words"))) {
                            //序号
                            job.put("numb", jsonArray.getJSONObject(j).getString("words"));
                            //体检人姓名
                            job.put("check_up_name", jsonArray.getJSONObject(j + 1).getString("words"));
                            //投保单号
                            job.put("insured_numb", jsonArray.getJSONObject(j + 2).getString("words"));
                            //投保人
                            job.put("insured_name", jsonArray.getJSONObject(j + 3).getString("words"));
                            //被保险人
                            job.put("assured_name", jsonArray.getJSONObject(j + 4).getString("words"));
                            //投保日期
                            job.put("insured_date", jsonArray.getJSONObject(j + 5).getString("words"));
                            //体检日期
                            job.put("check_up_date", jsonArray.getJSONObject(j + 6).getString("words"));
                            //体检项目
                            job.put("check_up_project", jsonArray.getJSONObject(j + 7).getString("words"));
                            //体检费用
                            job.put("check_up_price", jsonArray.getJSONObject(j + 8).getString("words"));
                            array.add(job);
                        }
                    }
                }
                if (object.getString("words").contains("制表人")) {
                    //制表人是否签名
                    jsonObject.put("creat_table", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
                if (object.getString("words").contains("部门负责人")) {
                    //营运部门负责人是否签名
                    jsonObject.put("division_principal", jsonArray.getJSONObject(j + 1).getString("words") != null && !jsonArray.getJSONObject(j + 1).getString("words").equals(""));
                }
            }
        }
        jsonObject.put("item", array);
        //表单类型
        jsonObject.put("table_type", "体检费用清单");
        return jsonObject;
    }


    private static final MediaType TYPE = MediaType.parse("application/json");

    /**
     * 发送通用表格识别请求
     *
     * @param base64string
     * @return
     */
    public String http(String base64string) {
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
            logger.error(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return content;
    }


}
