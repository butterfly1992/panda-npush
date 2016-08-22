package com.push.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.push.Service.impl.OperateServiceImpl;
import com.push.util.Utils;
import com.push.util.Variable;

/**
 * Servlet implementation class TakeOperServlet
 */
public class TakeOperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 获取request参数
		String imei = request.getParameter("imei");// 手机串码
		String imsi = request.getParameter("imsi");// 手机卡串码
		String appid = request.getParameter("appid");// 应用id
		String sid = request.getParameter("sid");// 产品id
		String key = request.getParameter("key");// 加密内容
		String result = "";
		// 参数验证

		if (Utils.isNULL(imei) || Utils.isNULL(imsi) || Utils.isNULL(appid) || Utils.isNULL(key)) {// 验证各项参数不能为空
			Utils.log.error("Valid null : " + imei + "；" + imsi + "；" + appid + "；" + key + "；");
			printJSON(response, Variable.errorJson);
			return;
		}
		String keycont = Utils.jiexiUrl(key, appid, imei);// 获取解密内容
		if (keycont == null) {// 解密失败
			Utils.log.error("JieXiException : " + key + ";appid:" + appid + ";imei:" + imei + "；");
			printJSON(response, Variable.errorJson);
			return;
		}
		String[] moreparam = keycont.split(";");
		String wareindex = moreparam[0];
		String oper = moreparam[1];
		String gysdkv = moreparam[2];
		Utils.log.info(".操作参数是： 『" + oper + "；" + wareindex + ";sid:" + sid + ";appid:" + appid + "』");
		if (Utils.isNULL(sid)) {
			OperateServiceImpl ops = new OperateServiceImpl();
			sid = ops.findId(wareindex);
			Utils.log.debug("softFind：" + sid + ";appid：" + appid);
			if (Utils.isNULL(sid)) {
				printJSON(response, Variable.errorJson);
				return;
			}
		}
		/*
		 * } 测试id统计展示和其他操作，但是不计费
		 */
		if (Variable.testId.contains(appid)) {
			Utils.log.info("测试id不统计");
			return;
		}
		if (imei.indexOf("00000") >= 0 || imei.indexOf("000000") >= 0 || Variable.errorImsi.contains(imei)) {//错误的串码
			Utils.log.info("Mobile moni： 『" + oper+ "imei:" +imei + ";imsi：" + imei
					+ ";appid:" +appid + "』");
			printJSON(response, Variable.errorJson);
			return;
		}
		/**
		 * 0,通知栏通知成功（开发者计费）
		 */
		try {
			OperateServiceImpl ops = new OperateServiceImpl();
			Utils.log.info("2.==此操作参数是： 『" + oper + "；传递参数：" + wareindex + ";sid:" + sid + ";appid:" + appid + "』");
			if (imsi.indexOf("00000") > 0) {
				Utils.log.info("Mobile moni： 『" + oper + "imsi：" + imsi + ";appid:" + appid + "』");
				return;
			}
			result = ops.getOperResult(sid, appid, imei, imsi, gysdkv, wareindex, oper);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 判断返回结果，不符合条件的则不返回数据处理
		if ("".equals(result) || null == result || "error".equals(result)) {
			Utils.log.error("返回结果为『" + result + "』");
			printJSON(response, Variable.errorJson);
			return;
		}
		printJSON(response, Variable.correntJson);
	}

	private void printJSON(HttpServletResponse response, String jsonStr) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
	}

}
