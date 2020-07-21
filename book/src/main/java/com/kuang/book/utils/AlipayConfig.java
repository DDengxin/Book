package com.kuang.book.utils;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016102200740889";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCArophf+BmguQ5l7T0/VcpRN5ZAaA2IBjkQKIsWNxh7MHVJWL8eyP6bzBd4+IZFDe1Em/aFiE3pT0T3fKFxr7lTIj0ftXNufarbH0KupwztzspXXR9uzxoUSY9AON65Vj8XU9GZxHM/efWiUuX4ttCFstx+UNbIa4bnUGtbUANy1CxSsvCrKNoQOSR1femHK7AIsKxEu9ytfmr2L8uJq/IhFB93OmQJj6xV+du+jlJ0tuXosgWRCI7HHy325ryvZcB3IrqxgJYpu4Kx0m+L2RS8QbrNYz87Iu9GGEjSCvptb7GL6vZr+diIN/k2/2xWAb7wMa096QERCJQMJQLZfqnAgMBAAECggEAHawhrKnYDMtR5RXeXD+uwY1n2A5P6ysrkjZ1LtSuvMXjTEr6sE2U1kKdXIkXscC/t7kwoZFIx5QKqXIcYkyZ6DXlvrsggffHsW+qxbaSHjq6IaezHOr32vYbYyW5TrVj941sokdSC3rg4s915uggtvSapQVdSr5Rb3yrkFoI0yUjF1hKdRiFlJ9DiI1i31MOBjBoAfODLTchBHjeYciY/iYD2fOXxMqdsU/tB+rdWT6giBTOz98AUUF1gCX+aVutFmFnPXBePe0WmAuVtHcDURwLVTiKkeir51C+B0BcFyj2olwKDahVciZMYiKNIaV+Ygr6xVqZ/MURsrg5M7BSQQKBgQC4Rd2HnY9f96f0R/R9Kgz8edQ4AATN3H7iU8Zt81vA4OXgVOk8Ft4QGXMhUfJLRC26qziLhnu13kDFaRpXT1kvQ+K5TtXHSdB+HKmvnduMOTC3Awg8SPaUq2FMWrpvwxEAs5sCImVGkl4+1lPjDbDB20p1maTlTSk5jOGMyguPCwKBgQCyxThh3lJXrlApNfGMKHrNoP9K0jLFSMscTB0cVNSCGi9phR2o/rrtNJjGQGdiGCTlLjAd2sNn4tvfZQ6JK/ZjXt2I/T/a3dulX8f9mSH51+YMH/I7P8OWlAIRzVL/YLJU/Ldqzid5vUQTs1gBodje0ONUfTbWtpQ6FFFmu0v0VQKBgC/nUbCooBYVnp7decxz+w5DaYzpTFZr5LxXhPUeV5LrpDbOPBe35iCPTSOzVuaEIdMuIbP70Ps1fOcU7JkX8ppb7nu359E3+jBeqSoTQnjQgT6CVki9uwRpDRE5YNAfCLOC/V/vqx5OfJufQg17iKDMri2mNHy7s2TXn+bmu1yDAoGAMdmjDk9zgLW/7q33LFFBZUPGzLNuqurQkJR87aCOHbV1kSQokC5wK6MmEup0qbr31tyZWEnfgYGWNGY2tnOG4lDaz89m1f5VuHjMCOCBw+y7Xpgt8O2n+b5OEW7C/SmWsPJDp0nTYjQv3pp9KP9bvU5chfvgpeEF1mhCBralyH0CgYBRFQIzW2Ibd2wPx4otJn9kDEvyvb2w/wg7PSHA8RMXS883elofE9CcBBZD6gfYX6FHE5fe1M9AgzdPpOtjsozLjn63p8assbLv7C33XhHyjXr/Txs428OYVuCIm63F7Ku6KcZsRzmUvdLeQl3ZyUHJDQHQfOBFtjpBZdPZgSFH4A==";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjDw0nCyj/hRXSUCsLBJLI/yfG8JpKt440Qgwme6+De7Mo1T9qYQGxkIy0Yf2+U/BlSU9uBLgYpMYPx1eNebJOfzQh1HrakD9hN4RxjxRwq4WBnaKw0v5Z3locFy5ZJUZuUoNLRFlqpy+2CrJXyvnoW3IZskd740lHOP3Byo6a5zy6oAVzJvEeQKK75PIB3gDACVsBpC5oPgY/Eq4NeWICtSlxC4ElkgSGs28KmwVmN0A/uhjnrmBOkHxwdGDDNb2VV3VAnZvTUftwse5uAm+VhoGyeAy/H69D2Onc5z9xbeXVeOfE6V5sn/dbrsFKHtE70NI5UKRQM+BCakY9J5sBwIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://182.92.113.2:8080/book/alipay/notify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://182.92.113.2:8080/book/alipay/return";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

