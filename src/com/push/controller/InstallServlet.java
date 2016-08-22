package com.push.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.push.Service.impl.InstallServiceImpl;
import com.push.util.Utils;
import com.push.util.Variable;

/**
 * Servlet implementation class InstallServlet
 */
public class InstallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Get请求直接return掉
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doPost(request, response);
		return;
	}

	/**
	 * Post请求处理
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		// 获取request参数
		String appid = request.getParameter("appid");
		String gysdkv = request.getParameter("gysdkv");
		String imei = request.getParameter("imei");
		String imsi = request.getParameter("imsi");
		String key = request.getParameter("key");
		String method = request.getMethod();
		String time = Utils.DateTime();
		String current_time = Utils.DateTime("yyyy-MM-dd HH:mm:ss");
		// 参数验证
		/**/
		if (Utils.isNULL(imei) || Utils.isNULL(imsi) || Utils.isNULL(appid)
				|| Utils.isNULL(gysdkv)) {
			Utils.log.error("Imei or imsi or appid or gysdkv is null : " + imei
					+ "|" + imsi + "|" + appid + "|" + gysdkv + "|");
			printJSON(response, Variable.errorJson);
			return;
		}

		if ("GET".equals(method)) {
			Utils.log.error("Request Method Error: " + method + "----imei : "
					+ imei);
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
			return;
		} else if (imei != null && imei.contains("?")) {
			printJSON(response, Variable.errorJson);
			return;
		}

		int i = 0;
		String sdkv = gysdkv;
		// 如果不是测试id,则统计装机量
		if (!Variable.testId.contains(appid)) {
			InstallServiceImpl installService = new InstallServiceImpl();
			i = installService.appInstall(imei, imsi, appid, time, sdkv);
		} else {
			printJSON(response, Variable.testId);
			return;
		}
		Utils.log.info(current_time + "result ：" + i
				+ " App install finish----------------------------------");
		if (i > 0)
			printJSON(response, Variable.correntJson);
		else
			printJSON(response, Variable.errorJson);
	}

	private void printJSON(HttpServletResponse response, String jsonStr)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
	}
}
