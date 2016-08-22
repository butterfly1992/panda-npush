package com.push.Service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.push.dao.OperateDao;
import com.push.dao.RequestDao;
import com.push.entity.App;
import com.push.entity.PushReq;
import com.push.entity.Soft;
import com.push.util.GeneralDB;
import com.push.util.IP;
import com.push.util.MemcacheUtil;
import com.push.util.Utils;

public class RequetServiceImpl {
	private RequestDao reqDao = DaoFactory.getInstance("com.push.dao.impl.RequestDaoImpl", RequestDao.class);
	private OperateDao operDao = DaoFactory.getInstance("com.push.dao.impl.OperateDaoImpl", OperateDao.class);
	GeneralDB db = new GeneralDB();

	// MemcacheUtil mcc = MemcacheUtil.getInstance();

	public boolean isOpenadv(String appid) {
		// TODO Auto-generated method stub
		App app = null;
		String sql = "select push_open_status from zy_app where id='" + appid + "'";
		app = reqDao.queryAPP(sql);
		if (app != null) {
			Integer lock_status = app.getPush_open_status();
			if (lock_status == 1) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	public String getResult(String appid, String imei, String imsi, String current_time, String gysdkv, String ip) {
		// TODO Auto-generated method stub
		String result = "";
		int[] push_status = null;
		String dateTime = Utils.DateTime();
		// 初始化每天的机会8个标识
		if (MemcacheUtil.mcc.get("newPush_statusFour" + imei + imsi+appid) != null) {
			push_status = (int[]) MemcacheUtil.mcc.get("newPush_statusFour" + imei + imsi+appid);// 判断机会类型是什么类型
		} else {// 生成有效广告次数，记录一次有效用户
			int pcount = reqDao.getCount(appid);
			push_status = new int[] { pcount };
			MemcacheUtil.mcc.set("newPush_statusFour" + imei + imsi+appid, push_status, getDefinedDateTime(23, 59, 59, 0));
			String sql = "update np_ori_actuser set imeicount=imeicount+1" + " where time='" + dateTime
					+ "' and gysdkv=" + gysdkv;
			int l = db.update(sql);
			if (l == 0) {
				sql = "insert into np_ori_actuser(imeicount,gysdkv,time) values (1,'" + gysdkv + "','" + dateTime
						+ "')";
				l = db.update(sql);
			}
			/* 判断此应用是否记录活跃用户 */
			/** 额外加的测试活跃用户量 */
			if (!imsi.equals("111111111111111")) {// 有效活跃用户
				if (MemcacheUtil.mcc.get("np_actout_" + appid) == null) {
					l = 1;
				} else {
					l = Integer.parseInt(MemcacheUtil.mcc.get("np_actout_" + appid).toString()) + 1;// 获取用户数
				}
				MemcacheUtil.mcc.set("np_actout_" + appid, l, getDefinedDateTime(23, 59, 59, 0));
				Utils.log.info("[pTappid：" + appid + "，npTActuser:" + l + "]");
			} else {// 无sim卡用户
				if (MemcacheUtil.mcc.get("np_factout_" + appid) == null) {
					l = 1;
				} else {
					l = Integer.parseInt(MemcacheUtil.mcc.get("np_factout_" + appid).toString()) + 1;// 获取用户数
				}
				MemcacheUtil.mcc.set("np_factout_" + appid, l, getDefinedDateTime(23, 59, 59, 0));
				Utils.log.info("[pFappid：" + appid + "，npFActuser:" + l + "]");
			}

		}
		Utils.log.info("『appid:" + appid + ";imei:" + imei + ";imsi:" + imsi + ";gysdkv:" + gysdkv + "』" + "小时对应状态【"
				+ current_time + ";" + push_status[0] + "】");
		result = getResult(appid, current_time, imei, imsi, push_status, gysdkv, ip);

		return result;
	}

	/**
	 * 新模式请求处理
	 * 
	 * @param current_time
	 * @param imei
	 * @param imsi
	 * @param wareindex
	 * @param push_status
	 * @param gysdkv
	 * @return
	 */
	private String getResult(String appid, String current_time, String imei, String imsi, int[] push_status2,
			String gysdkv, String ip) {
		// TODO Auto-generated method stub
		String result = "";
		if (push_status2[0] > 0) {//判断是否有机会展示广告
			result = returnPush(appid, imei, imsi, gysdkv, result, push_status2[0], ip);
		} else {
			return null;
		}

		return result;
	}

	/**
	 * 获取推送的产品，并处理Mem
	 * 
	 * @param imei
	 * @param imsi
	 * @param wareindex
	 * @param gysdkv
	 * @param result
	 * @return
	 */
	private String returnPush(String appid, String imei, String imsi, String gysdkv, String result, int flag,
			String ip) {
		Soft pushf = pushSoft(imei, imsi, ip);// 得到今天推送的产品信息
		if (pushf != null) {
			result = getJsonResult(pushf, gysdkv, imei, imsi);
			if (MemcacheUtil.mcc.get("newPush_statusFour" + imei + imsi+appid) != null) {
				Object o = MemcacheUtil.mcc.get("newPush_statusFour" + imei + imsi+appid);
				int[] push_status = null;
				if (o instanceof int[]) {
					push_status = (int[]) o;
				} else if (o instanceof Integer) {
					push_status = new int[] { (int) o };
				}
				push_status[0] = flag - 1;
				Utils.log.info("标志：=========" + push_status[0] + "======更新OK");
				MemcacheUtil.mcc.set("newPush_statusFour" + imei + imsi+appid, push_status,
						getDefinedDateTime(23, 59, 59, 0));
				Utils.log.error("小时对应状态【" + Utils.DateTime("HH") + ";" + push_status[0] + "】");
			}
		}
		return result;
	}

	/**
	 * 获取今天推送的数据
	 * 
	 * @param wareindex
	 * @param imei
	 * @param imsi
	 * @return
	 */
	private Soft pushSoft(String imei, String imsi, String ip) {
		Soft push = null;
		int operation = Utils.operators(imsi);
		Integer code = 0;
		Soft adv = null;
		PushReq req = null;
		int advsoftflag = 0;// 实体广告和产品的标识
		if (MemcacheUtil.mcc.get("npush_code" + imei + imsi) != null) {// 获取上次推送产品的优先级
			code = (Integer) MemcacheUtil.mcc.get("npush_code" + imei + imsi);
		}
		// 实体广告获取产品
		advsoftflag = code % 2;
		if ((advsoftflag == 1 || (code / 3 == 0)) && code != 0) {// 对二取余数后为1时或者是3的整数倍时返回实体广告
			if (MemcacheUtil.mcc.get("npush_adv_flag" + imei + imsi) == null) {// 今天尚未弹出过实体广告
				int priority = 0;// 获取优先级
				if (MemcacheUtil.mcc.get("npush_priority" + imei + imsi) != null) {// 对2取余数后为1时,或者是3的倍数时，查询实体数据，返回实体广告
					priority = (Integer) MemcacheUtil.mcc.get("npush_priority" + imei + imsi);
				}
				adv = reqDao.getPushadv(priority, operation);
				if (adv == null) {
					adv = reqDao.getPushadv(0, operation);
				}
				if (adv != null) {
					String sql = "select imei,imsi,ifnull(entindex,' ') entindex, time from np_ori_req where imei='"
							+ imei + "' and imsi='" + imsi + "' order by time desc";
					req = operDao.getReqIndex(sql);
					if (req != null) {
						String entindex = req.getEntindex();
						if (isContain(entindex, adv.getAdvindex() + "")) {
							MemcacheUtil.mcc.set("npush_priority" + imei + imsi, adv.getPriority());// 存放优先级
							MemcacheUtil.mcc.set("npush_adv_flag" + imei + imsi, false,
									getDefinedDateTime(23, 59, 59, 0));// 实体广告已经展示完
							return null;
						}
					}
				} else {
					MemcacheUtil.mcc.set("npush_adv_flag" + imei + imsi, false, getDefinedDateTime(23, 59, 59, 0));// 实体广告已经展示完
					return null;
				}
			}
		}
		if (adv != null) {// 判断有实体广告直接返回
			MemcacheUtil.mcc.set("npush_priority" + imei + imsi, adv.getPriority());// 存放优先级
			MemcacheUtil.mcc.set("npush_adv_flag" + imei + imsi, false, getDefinedDateTime(23, 59, 59, 0));// 实体广告已经展示完
			return adv;
		} else {
			push = reqDao.getPushsoft(code, operation);
			if (push == null) {
				push = reqDao.getPushsoft(0, operation);
			}

			if (operation > 0 && push.getSofttype() == 1) {// 屏蔽多省
				String area = "";
				if (MemcacheUtil.mcc.get("nlscr_area_" + ip) != null) {// 判断之前ip是否记录过
					area = (MemcacheUtil.mcc.get("nlscr_area_" + ip).toString());
				} else {
					IP.load(null);// 加载IP类
					String city = Arrays.toString(IP.find(ip));
					try {
						area = city.substring(3, 5);
					} catch (Exception e) {
						// TODO: handle exception
						Utils.log.info("ip:" + ip + ";city:" + city + "】");
						return null;
					}
					if (area.length() > 0)
						MemcacheUtil.mcc.set("nlscr_area_" + ip, area);// 将ip存储
				}
				Utils.log.info("ip:" + ip + ";area:" + area + "】");
				if (push.getProvince().contains(area)) {// 屏蔽地区之中，将此记录pass,
					Utils.log.error("【oneUnshow:" + area + "；soft:" + push.getProvince() + "】");
					MemcacheUtil.mcc.set("npush_code" + imei + imsi, push.getCode());
					return null;
				}
			}
			MemcacheUtil.mcc.set("npush_code" + imei + imsi, push.getCode());
			if (MemcacheUtil.mcc.get("npush_adv_flag" + imei + imsi) != null) {
				MemcacheUtil.mcc.delete("npush_adv_flag" + imei + imsi);//下次展示 实体广告
			}
			return push;
		}
	}

	private Date getDefinedDateTime(int hour, int minute, int second, int milliSecond) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milliSecond);
		Date date = new Date(cal.getTimeInMillis());
		return date;
	}

	/**
	 * 将推送的产品编译成json格式，返回客户端
	 * 
	 * @param soft
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getJsonResult(Soft soft, String gysdkv, String imei, String imsi) {
		JSONObject json = new JSONObject();
		int advsoft = soft.getAdvsoft();// 实体和产品广告标识
		if (advsoft == 0) {
			json.put("sid", soft.getId());
			json.put("sname", soft.getName());
			json.put("info2", soft.getInfo2());
			json.put("spck", soft.getPck());
			json.put("sindex", soft.getWareindex());
			json.put("title", soft.getInfo());
			json.put("info1", soft.getInfo1());
			json.put("icon", soft.getIcon());
			json.put("apk", soft.getApk());
			json.put("img01", soft.getImg1());
			json.put("img02", soft.getImg2());
			json.put("advsoft", 0);
			json.put("npway", soft.getNpway());
			String hadindex = String.valueOf(soft.getWareindex());
			int operation = Utils.operators(imsi);
			List<Soft> four = reqDao.getfour(hadindex, 4, hadindex, operation);
			// 随机数的四个
			if (four != null) {
				for (int i = four.size() - 1; i >= 0; i--) {
					Soft s = four.get(i);
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("sname", s.getName());
					m.put("sid", s.getId());
					m.put("spck", s.getPck());
					m.put("sindex", s.getWareindex());
					m.put("info2", s.getInfo2());
					m.put("info1", s.getInfo1());
					m.put("icon", s.getIcon());
					m.put("apk", s.getApk());
					json.put("" + (i + 1), m);
				}
			}
			Utils.log.info("『push-result：-【-_pck-:" + soft.getPck() + "-;--Name:" + soft.getName() + "--;code:-"
					+ soft.getCode() + ";bsize:{" + four.size() + "}】』");
			return json.toString();
		} else {
			json.put("aid", soft.getId());
			json.put("advname", soft.getName());
			json.put("info", soft.getInfo());
			json.put("linkurl", soft.getLinkurl());
			json.put("aindex", soft.getAdvindex());
			json.put("icon", soft.getIcon());
			json.put("advsoft", 1);
			Utils.log.info("『push-result：-【-advname-:" + soft.getName() + "-;--linkurl:" + soft.getLinkurl()
					+ "--;--priority-:" + soft.getPriority() + "】』");
			return json.toString();
		}

	}

	/**
	 * 测试手机返回的数据
	 */
	public String getTestImeiResult(String appid, String imei, String imsi, String current_time, String gysdkv) {
		// TODO Auto-generated method stub
		List<Soft> Softs = null;// 产品广告
		List<Soft> advs = null;// 实体广告

		boolean switchadv = true;
		if (MemcacheUtil.mcc.get("TestImei_flag" + imei + imsi) != null) {// 实体广告标识
			switchadv = (Boolean) MemcacheUtil.mcc.get("TestImei_flag" + imei + imsi);
		}

		String result = "";
		if (MemcacheUtil.mcc.get("TestImei_soft" + imei + imsi) != null) {// 判断mem中是否已经存储过了，存储过了直接提取
			Softs = (List<Soft>) MemcacheUtil.mcc.get("TestImei_soft" + imei + imsi);
		} else {
			Softs = reqDao.getPushsoft("0");
			MemcacheUtil.mcc.set("TestImei_soft" + imei + imsi, Softs, getDefinedDateTime(23, 59, 59, 0));
		}
		if (MemcacheUtil.mcc.get("TestImei_adv" + imei + imsi) != null) {
			// 判断mem中是否已经存储过了，存储过了直接提取
			advs = (List<Soft>) MemcacheUtil.mcc.get("TestImei_adv" + imei + imsi);
		} else {
			advs = reqDao.getPushadv();
			MemcacheUtil.mcc.set("TestImei_adv" + imei + imsi, advs, getDefinedDateTime(23, 59, 59, 0));
		}
		if (!(Softs == null || Softs.isEmpty())) {
			Soft push = Softs.get(0);

			if (push.getCode() >= 3 && push.getCode() % 3 == 1 && switchadv) {// 满足展示实体广告的条件，产品优先级为4，7并且实体开关打开后会出现实体广告
				if (!(advs == null || advs.isEmpty())) {
					Soft adv = advs.get(0);
					result = getJsonResult(adv, null, imei, imsi);
					advs.remove(0);
					MemcacheUtil.mcc.set("TestImei_adv" + imei + imsi, advs, getDefinedDateTime(23, 59, 59, 0));
					MemcacheUtil.mcc.set("TestImei_flag" + imei + imsi, false, getDefinedDateTime(23, 59, 59, 0));
				} else {
					MemcacheUtil.mcc.delete("TestImei_adv" + imei + imsi);//删除空对象，下轮重新提取数据
					MemcacheUtil.mcc.set("TestImei_flag" + imei + imsi, false, getDefinedDateTime(23, 59, 59, 0));
				}

			} else {
				result = getJsonResult(push, null, imei, imsi);
				Softs.remove(0);
				MemcacheUtil.mcc.set("TestImei_soft" + imei + imsi, Softs, getDefinedDateTime(23, 59, 59, 0));//删除空对象，下轮重新提取数据
				MemcacheUtil.mcc.set("TestImei_flag" + imei + imsi, true, getDefinedDateTime(23, 59, 59, 0));
			}
		} else {
			MemcacheUtil.mcc.delete("TestImei_soft" + imei + imsi);
		}
		return result;
	}

	public boolean isContain(String s, String c) {
		String a[] = s.split(",");
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 黑名单处理方法
	 * 
	 * @param appid
	 *            应用id
	 * @param imei
	 *            手机串码
	 * @param imsi
	 *            手机卡串码
	 * @param current_time
	 *            当前时间
	 * @param gysdkv
	 *            版本
	 * @return
	 */
	public String getblacklistResult(String appid, String imei, String imsi, String current_time, String gysdkv) {
		// TODO Auto-generated method stub
		List<Soft> softs = null;// 符合条件产品广告
		Soft push = null;
		String result = "";
		if (MemcacheUtil.mcc.get("np_blacklist") != null) {// 存在黑名单推送的产品
			softs = (List<Soft>) MemcacheUtil.mcc.get("np_blacklist");
		} else {// 不存在，从数据库读取
			softs = reqDao.getBlacklistsoft();
		}
		if (softs.size() > 0) {// 有产品列表
			push = softs.get(0);
			softs.remove(0);
			result = getJsonResult(push, null, imei, imsi);
			MemcacheUtil.mcc.set("np_blacklist", softs, getDefinedDateTime(23, 59, 59, 0));
		} else {// 暂无产品,删除此key
			MemcacheUtil.mcc.delete("np_blacklist");
		}
		return result;
	}
}
