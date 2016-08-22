package com.push.dao.impl;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.push.dao.OperateDao;
import com.push.entity.PushReq;
import com.push.entity.Soft;
import com.push.util.Utils;

public class OperateDaoImpl implements OperateDao {

	private QueryRunner qr = new QueryRunner(DbUtils.getDataSource());

	public int update(String sql) {
		int l = 0;
		try {
			l = qr.update(sql);
//			Utils.log.info(sql + "\n" + " result:[" + l + "]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static void main(String[] aa) throws Exception {
		// QueryRunner qr = new QueryRunner(DbUtils.getDataSource());
		// String sql = "select time from ori_req2264 ";
		// // List<Operate> o = qr.query(sql, new BeanListHandler<Operate>(
		// // Operate.class));
	}

	@Override
	public int recordNotice(String appid, String dateTime, String gysdkv)
			throws SQLException {
		// TODO Auto-generated method stub
		String sql = "update np_ori_noticesetup set noticec=noticec +1 where appid=? and time=? and gysdkv=?";
		int i = qr.update(sql, appid, Utils.DateTime(), gysdkv);
		if (i == 0) {
			sql = "insert into np_ori_noticesetup(appid,noticec,setcount,time,gysdkv) values (?,1,0,?,?)";
			i = qr.update(sql, appid, Utils.DateTime(), gysdkv);
		}
//		Utils.log.info(sql +"\n result:" + i);
		return i;
	}

	@Override
	public int recordSetup(String appid, String gysdkv, String dateTime)
			throws SQLException {
		// TODO Auto-generated method stub
		String sql = "update np_ori_noticesetup set setcount =setcount +1 where appid=? and time=? and gysdkv=?";
		int i = qr.update(sql, appid, Utils.DateTime(), gysdkv);
		if (i == 0) {
			sql = "insert into np_ori_noticesetup(appid,noticec,setcount ,time,gysdkv) values (?,0,1,?,?)";
			i = qr.update(sql, appid, Utils.DateTime(), gysdkv);
		}
		Utils.log.info(sql + appid + "—" + dateTime + "\n result:" + i);
		return i;
	}

	@Override
	public PushReq getReqIndex(String sql) {
		// TODO Auto-generated method stub
		PushReq req = null;
		try {
			req = qr.query(sql, new BeanHandler<PushReq>(PushReq.class));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

	@Override
	public int insertNotice(String imei, String imsi, String wareindex) {
		// TODO Auto-generated method stub
		int l = 0;
		String sql = " insert into np_ori_req (imei,imsi,nindex,time) values (?,?,?,?)";
		try {
			l = qr.update(sql, imei, imsi, wareindex, Utils.DateTime());
//			Utils.log.info(sql + "\n" + " result:[" + l + "]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public int operIndex(String sql, String imei, String imsi, String time) {
		// TODO Auto-generated method stub
		int l = 0;
		try {
			l = qr.update(sql, imei, imsi, time);
			//Utils.log.info(sql + "\n" + " result:[" + l + "]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public String findId(String sql, String pck) {
		// TODO Auto-generated method stub
		try {
			Soft soft = qr.query(sql, new BeanHandler<Soft>(Soft.class), pck);
			if (soft != null) {
				return soft.getId();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public int delIndex(String sql, String imei, String imsi) {
		// TODO Auto-generated method stub
		int l = 0;
		try {
			l = qr.update(sql, imei, imsi);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}
	@Override
	public int insertEntnotice(String imei, String imsi, String wareindex) {
		// TODO Auto-generated method stub
		int l = 0;
		String sql = " insert into np_ori_req (imei,imsi,entindex,time) values (?,?,?,?)";
		try {
			l = qr.update(sql, imei, imsi, wareindex, Utils.DateTime());
//			Utils.log.info(sql + "\n" + " result:[" + l + "]");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}
}
