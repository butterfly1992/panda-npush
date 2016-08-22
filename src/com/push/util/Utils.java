package com.push.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mbl.base64.BackAES;

public class Utils {

	public static int PANDA_ADV_TIMEFLAG = 2;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	public static Logger log = Logger.getLogger("Utils");

	public static String getID(String tem_name) {
		String id = "";
		int nums = 0;
		for (int i = 0; i < tem_name.length(); i++) {
			int num = (int) tem_name.charAt(i);// 返回指定索引处的char值
			nums += num;
		}
		for (int i = 0; i < 4; i++) {
			id = id + new Random().nextInt(9);
		}
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String ctime = format.format(new Date());
		id = ctime + nums + id;
		return id;
	}

	/**
	 * null检测
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNULL(String str) {
		if (str == null || str.length() <= 0 || str.equals("null") || str.equals("NULL")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转码
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String toIso(String str) throws IOException {
		str = new String(str.getBytes("UTF-8"), "iso-8859-1");
		return str;
	}

	public static String toUtf(String str) throws IOException {
		if (str == null)
			return "";
		str = new String(str.getBytes("iso-8859-1"), "UTF-8");
		return str;
	}

	public static String toGB(String str) throws IOException {
		str = new String(str.getBytes("iso-8859-1"), "GB2312");
		return str;
	}

	/**
	 * 获得hashcode
	 * 
	 * @param keyword
	 * @return
	 */
	public static int Change(String keyword) {
		int nums = 0;
		for (int i = 0; i < keyword.length(); i++) {
			int num = (int) keyword.charAt(i);// 返回指定索引处的char值
			nums += num;
		}
		return nums;
	}

	/**
	 * 检测生肖等
	 * 
	 * @param item
	 * @param para
	 * @param len
	 * @return
	 */
	public static String checkItem(String item, String para, int len) {
		return checkItem("item", item, para, len);
	}

	public static String checkItem(String paraItem, String item, String para, int len) {
		String msg = "";
		if (isNULL(item)) {
			msg = paraItem + "(" + para + ")参数不可以为空";
		} else if (!item.matches("^\\d+$")) {
			msg = paraItem + "(" + para + ")参数不正确";
		} else {
			int name_num = Integer.parseInt(item);
			if (name_num > len || name_num <= 0) {
				msg = paraItem + "(" + para + ")参数不正确";
			}
		}
		return msg;
	}

	/**
	 * 检测日期
	 * 
	 * @param nm
	 * @return
	 */
	public static String checkDate(String nm) {
		String reg = "^[0-9]{4}-[0-1]{0,1}[0-9]{1}-[0-3]{0,1}[0-9]{1}$";
		if (isNULL(nm)) {
			return "(日期)参数不可以为空";
		}
		if (!nm.matches(reg)) {
			return "(日期)格式不正确，应为xxxx-x-x";
		}
		return "";
	}

	public static String checkName(String name, int num) throws IOException {
		return checkName(name, num, num);
	}

	public static String checkName(String name, int num1, int num2) throws IOException {
		String msg = "";
		if (!Utils.isNULL(name)) {
			String nm = Utils.toUtf(name);
			if (num1 == num2 && nm.length() != num1) {
				msg = "只能填" + num1 + "个字！";
			} else if (nm.length() < num1 || nm.length() > num2) {
				msg = "只能填" + num1 + "-" + num2 + "个字！";
			} else if (!Utils.isChinese(nm)) {
				msg = "只能是汉字！";
			}
		} else {
			msg = "不能为空！";
		}
		return msg;
	}

	/**
	 * 获取是对应格式的当天日期
	 * 
	 * @return
	 */
	public static String DateTime(String mate) {
		// yyyyMMdd hh:mm:ss
		SimpleDateFormat format = new SimpleDateFormat(mate);
		Date date = new Date();
		String s = format.format(date);
		return s;
	}

	public static String getYesterday(String mate) {
		// yyyyMMdd hh:mm:ss
		SimpleDateFormat format = new SimpleDateFormat(mate);
		Date date = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
		String s = format.format(date);
		return s;
	}

	public static String DateTime() {
		// yyyy-MM-dd
		Date date = new Date();
		String s = format.format(date);
		return s;
	}

	/**
	 * 获取是对应格式的日期
	 * 
	 * @return
	 */
	public static String DateTime(String mate, String date) {
		// yyyy-M-d
		SimpleDateFormat format = new SimpleDateFormat(mate);
		Date d = null;
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		String s = format.format(d);
		return s;
	}

	/**
	 * 获取是本月的第几周
	 * 
	 * @return
	 */
	public static int getWeek() {
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.WEEK_OF_MONTH);
		return week;
	}

	/**
	 * 获致是本周的今天周几, 1代表星期天...7代表星期六
	 * 
	 * @return
	 */
	public static String getTodayOfWeek() {
		String[] weeks = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		return "星期" + weeks[day - 1];// day=1,表示星期日
	}

	/**
	 * 获致是本周的第明天星期几, 1代表星期天...7代表星期六
	 * 
	 * @return
	 */
	public static String getTomorrowOfWeek() {
		String[] weeks = { "", "一", "二", "三", "四", "五", "六", "日" };
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);// day=1,表示星期日
		return "星期" + weeks[day];
	}

	/**
	 * 得到本月的第一天
	 * 
	 * @return
	 */
	public static String getMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	/**
	 * 得到本月的最后一天
	 * 
	 * @return
	 */
	public static String getMonthLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	/**
	 * 获取本月总共的周数
	 * 
	 * @return
	 */
	public static int getWeekCount() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = format.parse(getMonthLastDay());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int count = c.get(Calendar.WEEK_OF_MONTH);// 获取是本月的第几周
		int day = c.get(Calendar.DAY_OF_WEEK);
		if (day != 7) {
			count--;
		}
		return count;
	}

	/**
	 * 字符串转码
	 * 
	 * @return
	 */
	public static String formatStr(String str) {
		String strs = "";
		try {
			strs = new String(str.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 判断是不是汉字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChinese(String str) {
		return str.matches("^[\u4e00-\u9fa5]+$");
	}

	public static String uuID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static Map<String, String> getReq(String queryString, HttpServletRequest request) {
		Map<String, String> req = new HashMap<String, String>();
		try {
			if (queryString != null) {
				queryString = new String(queryString.getBytes("iso-8859-1"), request.getCharacterEncoding())
						.replace("''", " ");
				String[] arrayQuery = queryString.split("&");
				for (int i = 0; i < arrayQuery.length; i++) {
					String[] temp = arrayQuery[i].split("=");
					if (temp.length == 1) {
						req.put(temp[0], "");
					} else if (temp.length == 2) {
						req.put(temp[0], temp[1]);
					}
				}
			}
		} catch (Exception e) {
		}
		return req;
	}

	public static String getMD5(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	public static String getCodeid(String str) {
		return (str.hashCode() + "").replace("-", "");
	}

	public static int getPANDA_ADV_TIMEFLAG() {
		System.out.println("Get Time Flag:" + PANDA_ADV_TIMEFLAG);
		return PANDA_ADV_TIMEFLAG;
	}

	public static void setPANDA_ADV_TIMEFLAG(int pANDA_ADV_TIMEFLAG) {
		PANDA_ADV_TIMEFLAG = pANDA_ADV_TIMEFLAG;
		System.out.println("Set Time Flag:" + PANDA_ADV_TIMEFLAG);
	}

	/**
	 * 判断运营商
	 * 
	 * @param reqdao
	 */
	public static int operators(String imsi) {
		// 判断是何种运营商 0:所有 1:移动 2:联通 3:电信 4:铁通
		Integer carrieroperator = 0;
		String mnc = imsi.substring(0, 5);
		if ("46000".equals(mnc) || "46002".equals(mnc) || "46007".equals(mnc)) {
			// 是移动用户
			carrieroperator = 1;
		} else if ("46001".equals(mnc) || "46006".equals(mnc)) {
			// 是联通用户
			carrieroperator = 2;
		} else if ("46003".equals(mnc) || "46005".equals(mnc)) {
			// 是电信用户
			carrieroperator = 3;
		} else if ("46020".equals(mnc)) {
			// 是铁通用户
			carrieroperator = 0;
		}
		return carrieroperator;
	}

	/**
	 * 解析路径获得参数
	 * 
	 * @param 加密内容
	 * @param 应用id
	 * @param 用户设备串码
	 * @return
	 */
	public static String jiexiUrl(String key, String appid, String imei) {
		
		String decryptString = null;
		try {
			String skey = "p_" + appid.substring(0, 8) + imei.substring(imei.length() - 3, imei.length());
			decryptString = BackAES.decrypt(key, skey, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptString;
	}
}
