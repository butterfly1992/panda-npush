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
 * Servlet implementation class OperateServlet
 */
public class OperateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * getReturn
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return;
		// doPost(request, response);
	}

	/**
	 * post请求
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 获取request参数
		String imei = request.getParameter("imei");// 手机串码
		String imsi = request.getParameter("imsi");// 手机卡串码

		String appid = request.getParameter("appid");// 应用id
		String sid = request.getParameter("sid");// 应用的包名
		String gysdkv = request.getParameter("gysdkv");// 应用版本

		String wareindex = request.getParameter("wareindex");// 操作的产品的索引
		String oper = request.getParameter("operate");
		String key = request.getParameter("key");
		String result = "";
		// 参数验证
		/**/

		Utils.log.info("==1.操作参数是： 『" + oper + "；1.传递参数：" + wareindex
				+ ";1.sid:" + sid + ";1.appid:" + appid + "』");
		if (Utils.isNULL(imei) || Utils.isNULL(imsi) || Utils.isNULL(appid)
				|| Utils.isNULL(gysdkv)) {
			Utils.log.error("操作参数 ：" + oper
					+ "======Imei or imsi or appid    or gysdkv is null : "
					+ imei + "；" + imsi + "；" + appid + "；" + gysdkv + "；");
			printJSON(response, Variable.errorJson);
			return;
		}

		if (!Utils.getMD5(imei).equals(key)) {
			Utils.log.error("Key Error|key:" + imei);
			printJSON(response, Variable.errorJson);
			return;
		}
		if ("".equals(imei)) {
			imei = "null";
		} else if (imei != null && imei.contains("?")) {
			printJSON(response, Variable.errorJson);
			return;
		}
		if (imei.indexOf("00000") >= 0 || imei.indexOf("000000") >= 0 || Variable.errorImsi.contains(imei)) {//错误的串码
			Utils.log.info("Mobile moni： 『" + oper+ "imei:" +imei + ";imsi：" + imei
					+ ";appid:" +appid + "』");
			printJSON(response, Variable.errorJson);
			return;
		}
		if (oper == null) {
			Utils.log.error("oper Error|oper:" + oper);
			printJSON(response, Variable.errorJson);
			return;
		}

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
		/**
		 * 0,通知栏通知成功（开发者计费）
		 */
		try {
			OperateServiceImpl ops = new OperateServiceImpl();
			Utils.log.info("2.==此操作参数是： 『" + oper + "；传递参数：" + wareindex
					+ ";sid:" + sid + ";appid:" + appid + "』");
			if (imsi.indexOf("00000") > 0) {
				Utils.log.info("Mobile moni： 『" + oper + "imsi：" + imsi
						+ ";appid:" + appid + "』");
				return;
			}
			result = ops.getOperResult(sid, appid, imei, imsi, gysdkv,
					wareindex, oper);
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

	private void printJSON(HttpServletResponse response, String jsonStr)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
	}

}
