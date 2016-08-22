package com.push.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.push.Service.impl.OperateServiceImpl;
import com.push.util.MemcacheUtil;
import com.push.util.Utils;
import com.push.util.Variable;

/**
 * Servlet implementation class Memdate
 */
public class Memdate extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String imei = request.getParameter("imei");
		String imsi = request.getParameter("imsi");
		String code = request.getParameter("code");// push广告优先级
		String oper = request.getParameter("oper");// clear才能清除用户索引
		String adv = request.getParameter("adv");// 实体广告标识
		boolean flag = false;
		boolean flag2 = false;// 用于实体广告adv
		if (Utils.isNULL(imsi)) {
			imsi = "111111111111111";// 如果没sim卡，用临时的号
		}
		if (MemcacheUtil.mcc.keyExists("newPush_statusFour" + imei + imsi)) {
			flag = MemcacheUtil.mcc.delete("newPush_statusFour" + imei + imsi);
		}
		if (adv != null) {
			flag2 = MemcacheUtil.mcc.delete("npush_adv_flag" + imei + imsi);
		}
		if (code != null) {// 清除push，从头再来
			if (MemcacheUtil.mcc.keyExists("npush_code" + imei + imsi)) {
				flag = MemcacheUtil.mcc.delete("npush_code" + imei + imsi);
			}
		}
		if (!Utils.isNULL(oper)) {
			if (oper.equals("clear")) {// 清除push用户索引
				OperateServiceImpl operser = new OperateServiceImpl();
				int res = operser.delIndex(imei, imsi);
			}
		}
		if (flag || flag2)
			printJSON(response, "『clearsoft:" + flag + "  ;clearadv:" + flag2
					+ "』");
		else
			printJSON(response, Variable.errorJson);
		return;
	}

	private void printJSON(HttpServletResponse response, String jsonStr)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
	}
}
