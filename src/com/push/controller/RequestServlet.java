package com.push.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.push.Service.impl.RequetServiceImpl;
import com.push.util.Utils;
import com.push.util.Variable;

import user.agent.tool.Platform;

/**
 * Servlet implementation class Re
 */
public class RequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @return 掉
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		return;
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 获取request参数
		String appid = request.getParameter("appid");
		String gysdkv = request.getParameter("gysdkv");
		String version = request.getParameter("version");
		String imei = request.getParameter("imei");
		String imsi = request.getParameter("imsi");
		String key = request.getParameter("key");
		String current_time = Utils.DateTime("HH");
		String result = "";
		// 参数验证
		/**/
		/*版本key更换*/
		if (Utils.isNULL(imei) || Utils.isNULL(appid)||Utils.isNULL(gysdkv)) {
			Utils.log.error("『Imei or   appid or gysdkv is null  : " + imei
					+ "；" + appid + "；" + gysdkv + "；』");
			printJSON(response, Variable.errorJson);
			return;
		}
		if (!Utils.getMD5(imei).equals(key)) {
			Utils.log.error("Key Error|key:" + imei + ";key:" + key + ";md:"
					+ Utils.getMD5(imei));
			printJSON(response, Variable.errorJson);
			return;
		}
		if ("".equals(imei)) {
			imei = "null";
		} else if (imei != null && imei.contains("?")) {
			printJSON(response, Variable.errorJson);
			return;
		}
		if (Utils.isNULL(imsi)) {
			imsi = "111111111111111";// 如果没sim卡，用临时的号
		}
		RequetServiceImpl reqs = new RequetServiceImpl();
		if (Variable.testId.contains(appid)) {// 如果是测试id或者测试手机，轮换返回产品
			result = reqs.getTestImeiResult(appid, imei, imsi, current_time,
					gysdkv);
			if ("".equals(result) || null == result || "error".equals(result)) {
				printJSON(response, Variable.errorJson);
				return;
			}
			printJSON(response, result);
			return;
		}
		/*-----------------------拦截黑名单应用，执行以下操 作------------------------------------------------------------------------------*/
		if (Variable.blacklistId.contains(appid)) {// 如果是测试id或者测试手机，轮换返回产品
			result = reqs.getblacklistResult(appid, imei, imsi, current_time,
					gysdkv);
			if ("".equals(result) || null == result || "error".equals(result)) {
				printJSON(response, Variable.errorJson);
				return;
			}
			printJSON(response, result);
			return;
		}
		/*-----------------------拦截黑名单结束End------------------------------------------------------------------------------*/
		/*-----------------------正式id执行以下操 作------------------------------------------------------------------------------*/
		// 判断是否开启push广告
		boolean isopenAdv = false;
		if (!Variable.testId.contains(appid)) {
			isopenAdv = reqs.isOpenadv(appid);
		}
		if (isopenAdv) {// 开启广告，查询数据
			result = reqs.getResult(appid, imei, imsi, current_time, gysdkv,Platform.findIp(request));
		} else {
			Utils.log.error("swtich close;appid:" + appid + "；");
			printJSON(response, Variable.errorJson);
			return;
		}
		// ""为已经展示过 "none"为查询结果为空 "error"为查询时异常
		if ("".equals(result) || null == result || "error".equals(result)) {
			Utils.log.error("返回结果为【" + result + "】");
			printJSON(response, Variable.errorJson);
			return;
		} else
			printJSON(response, result);
	}

	private void printJSON(HttpServletResponse response, String jsonStr)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
	}

}
