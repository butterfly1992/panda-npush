package com.push.Service.impl;

import java.sql.SQLException;

import com.push.dao.OperateDao;
import com.push.entity.PushReq;
import com.push.util.Utils;
import com.push.util.Variable;

public class OperateServiceImpl {

	private OperateDao operDao = DaoFactory.getInstance(
			"com.push.dao.impl.OperateDaoImpl", OperateDao.class);

	/**
	 * 操作处理方法
	 * 
	 * @throws SQLException
	 */
	public String getOperResult(String sid, String appid, String imei,
			String imsi, String gysdkv, String wareindex, String oper)
			throws SQLException {
		// 判断是什么操作，根据不同操作处理不同的业务
		
		String dateTime = Utils.DateTime();
		PushReq req = null;
		String sql = "";
		int flag = 0;
		if (oper.equals("0")) {// 处理到通知栏 ,记录通知栏量
			// 从数据库中根据用户信息查询记录
			sql = "select imei,imsi,ifnull(nindex,' ') nindex, time from np_ori_req where imei='"
					+ imei + "' and imsi='" + imsi + "' order by time desc";
			req = operDao.getReqIndex(sql);

			if (req == null) {// 第一次通知
				flag = operDao.insertNotice(imei, imsi, wareindex);
			} else {
				String nindex = req.getNindex();
				String time = req.getTime();
				if (isContain(nindex, wareindex))//已经通知过了，不再记录
					return null;
				else {
					sql = "update np_ori_req set nindex=concat(ifnull(nindex,'0'),',"
							+ wareindex
							+ "') where imei=? and imsi=? and time=?";
					flag = operDao.operIndex(sql, imei, imsi, time);
				}
			}
			if (flag > 0) {// 更新通知量
				flag = operDao.recordNotice(appid, dateTime, gysdkv);
				sql = "update np_ori_aoper set  notice=notice+1  where sid='"
						+ sid + "' and time='" + dateTime + "'  and appid='"
						+ appid + "'  and gysdkv='" + gysdkv + "'";
				flag = operDao.update(sql);
				if (flag == 0) {
					sql = "insert into np_ori_aoper(appid,sid,click,download,screen,setup,notice,time,gysdkv) values('"
							+ appid
							+ "','"
							+ sid
							+ "',0,0,0,0,1,'"
							+ dateTime
							+ "','" + gysdkv + "')";
					flag = operDao.update(sql);
				}
			}
		} else if (oper.equals("1")) {// 全屏查看点击查看详情
			sql = "update np_ori_aoper set  screen=screen+1  where sid='" + sid
					+ "' and time='" + dateTime + "'  and appid='" + appid
					+ "'  and gysdkv='" + gysdkv + "' ";
			flag = operDao.update(sql);//更新全屏
			if (flag == 0) {//更新失败，插入数据
				sql = "insert into np_ori_aoper(appid,sid,click,download,screen,setup,time,gysdkv) values('"
						+ appid
						+ "','"
						+ sid
						+ "',0,0,1,0,'"
						+ dateTime
						+ "','" + gysdkv + "')";
				flag = operDao.update(sql);
			}
		} else if (oper.equals("2")) {// 处理点击下载
			 
			sql = "update np_ori_aoper set click=click+1 where sid='" + sid
					+ "' and time='" + dateTime + "'  and appid='" + appid
					+ "' and gysdkv='" + gysdkv + "'";
			flag = operDao.update(sql);
			if (flag == 0) {
				sql = "insert into np_ori_aoper(appid,sid,click,download,screen,setup,time,gysdkv) values('"
						+ appid
						+ "','"
						+ sid
						+ "',1,0,0,0,'"
						+ dateTime
						+ "','" + gysdkv + "')";
				flag = operDao.update(sql);
			}
		} else if (oper.equals("3")) {// 处理下载完成
			sql = "update np_ori_aoper set download=download+1 where sid='"
					+ sid + "' and time='" + dateTime + "'  and appid='"
					+ appid + "' and gysdkv='" + gysdkv + "'";
			flag = operDao.update(sql);
			if (flag == 0) {
				sql = "insert into np_ori_aoper(appid,sid,click,download,screen,setup,time,gysdkv) values('"
						+ appid
						+ "','"
						+ sid
						+ "',0,1,0,0,'"
						+ dateTime
						+ "','" + gysdkv + "')";
				flag = operDao.update(sql);
			}
		} else if (oper.equals("4")) {// 处理安装完成
			sql = "select imei,imsi,ifnull(setindex,' ') setindex, time from np_ori_req where imei='"
					+ imei + "' and imsi='" + imsi + "' order by time desc";
			req = operDao.getReqIndex(sql);//查询用户的安装记录
			if (req == null) {
				return null;// 此用户之前未有记录
			} else {
				String setindex = req.getSetindex();
				String time = req.getTime();
				if (isContain(setindex, wareindex))// 已安装过
					return null;
				else {//尚未安装过，记录安装索引
					sql = "update np_ori_req set setindex=concat(IFNULL(setindex,''),',"
							+ wareindex
							+ "') where imei=? and imsi=? and time=?";
					flag = operDao.operIndex(sql, imei, imsi, time);
				}
			}
			if (flag > 0) {
				sql = "update np_ori_aoper set setup=setup+1 where sid='" + sid
						+ "' and time='" + dateTime + "'  and appid='" + appid
						+ "' and gysdkv='" + gysdkv + "'";
				flag = operDao.update(sql);
				if (flag == 0) {
					sql = "insert into np_ori_aoper(appid,sid,click,download,screen,setup,time,gysdkv) values('"
							+ appid
							+ "','"
							+ sid
							+ "',0,0,0,1,'"
							+ dateTime
							+ "','" + gysdkv + "')";
					flag = operDao.update(sql);
				}
			}
			if (flag > 0) {
				flag = operDao.recordSetup(appid, gysdkv, dateTime);
			}
		} else if (oper.equals("5")) {// 实体广告展示到通知栏
			// 从数据库中根据用户信息查询记录
			sql = "select imei,imsi,ifnull(entindex,' ') entindex, time from np_ori_req where imei='"
					+ imei + "' and imsi='" + imsi + "' order by time desc";
			req = operDao.getReqIndex(sql);
			if (req == null) {// 第一次通知实体广告
				flag = operDao.insertEntnotice(imei, imsi, wareindex);
			} else {
				String entindex = req.getEntindex();
				String time = req.getTime();
				if (isContain(entindex, wareindex))//实体广告已经通知过
					return null;
				else {
					sql = "update np_ori_req set entindex=concat(IFNULL(entindex,''),',"
							+ wareindex
							+ "') where imei=? and imsi=? and time=?";
					flag = operDao.operIndex(sql, imei, imsi, time);
				}
			}
			sql = "update np_ori_eoper set  notice=notice+1  where sid='" + sid
					+ "' and time='" + dateTime + "'  and appid='" + appid
					+ "' and gysdkv='" + gysdkv + "'";
			flag = operDao.update(sql);
			if (flag == 0) {
				sql = "insert into np_ori_eoper(appid,sid,notice,detial,time,gysdkv) values('"
						+ appid
						+ "','"
						+ sid
						+ "',1,0,'"
						+ dateTime
						+ "','"
						+ gysdkv + "')";
				flag = operDao.update(sql);
			}
		} else if (oper.equals("6")) {// 实体广告全屏
			// 从数据库中根据用户信息查询记录
			sql = "select imei,imsi,ifnull(delindex,' ') delindex, time from np_ori_req where imei='"
					+ imei + "' and imsi='" + imsi + "' order by time desc";
			req = operDao.getReqIndex(sql);
			if (req == null) {// 第一次全屏实体广告
				Utils.log.error("reqnull_" + appid);
				return null;
			} else {
				String delindex = req.getDelindex();
				String time = req.getTime();
				if (isContain(delindex, wareindex)) {
					// 已经查看过此实体广告
					return null;
				} else {
					sql = "update np_ori_req set delindex=concat(IFNULL(delindex,''),',"
							+ wareindex
							+ "') where imei=? and imsi=? and time=?";
					flag = operDao.operIndex(sql, imei, imsi, time);
				}
			}
			sql = "update np_ori_eoper set  detial=detial+1  where sid='" + sid
					+ "' and time='" + dateTime + "'  and appid='" + appid
					+ "' and gysdkv='" + gysdkv + "'";//更新实体广告查看
			flag = operDao.update(sql);
			if (flag == 0) {
				sql = "insert into np_ori_eoper(appid,sid,notice,detial,time,gysdkv) values('"
						+ appid
						+ "','"
						+ sid
						+ "',0,1,'"
						+ dateTime
						+ "','"
						+ gysdkv + "')";
				flag = operDao.update(sql);
			}
		}

		if (flag > 0) {
			return Variable.correntJson;
		} else
			return Variable.errorJson;
	}

	/**
	 * 根据索引查找id
	 * 
	 * @param wareindex
	 * @return
	 */
	public String findId(String wareindex) {
		// TODO Auto-generated method stub
		String sql = " SELECT id FROM zy_soft  WHERE wareindex=?";
		return operDao.findId(sql, wareindex);
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
	 * 内测删除记录索引
	 * 
	 * @param imei
	 * @param imsi
	 * @return
	 */
	public int delIndex(String imei, String imsi) {
		// TODO Auto-generated method stub
		String sql = "delete from np_ori_req where imei=? and imsi=?";
		int res = operDao.delIndex(sql, imei, imsi);
		return res;
	}
}
