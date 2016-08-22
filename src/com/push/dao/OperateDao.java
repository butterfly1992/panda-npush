package com.push.dao;

import java.sql.SQLException;

import com.push.entity.PushReq;

public interface OperateDao {
	public PushReq getReqIndex(String sql);

	public int update(String sql);

	/**
	 * 记录通知
	 * 
	 * @param appid
	 * @param gysdkv
	 * @param dateTime
	 * @return
	 * @throws SQLException
	 */
	public int recordNotice(String appid, String gysdkv, String dateTime)
			throws SQLException;

	/**
	 * 记录安装
	 * 
	 * @param appid
	 * @param gysdkv
	 * @param dateTime
	 * @return
	 * @throws SQLException
	 */
	public int recordSetup(String appid, String gysdkv, String dateTime)
			throws SQLException;

	/**
	 * 插入索引表
	 * @param imei
	 * @param imsi
	 * @param wareindex
	 * @return
	 */
	public int insertNotice(String imei, String imsi, String wareindex);

	/**
	 * 更新索引表
	 * @param sql
	 * @param imei
	 * @param imsi
	 * @param time
	 * @return
	 */
	public int operIndex(String sql, String imei, String imsi, String time);

	/**
	 * 根据包名查询出索引
	 * @param sql
	 * @param pck
	 * @return
	 */
	public String findId(String sql, String pck);

	public int delIndex(String sql, String imei, String imsi);

	/**
	 * 实体广告记录通知
	 * @param imei
	 * @param imsi
	 * @param wareindex
	 * @return
	 */
	public int insertEntnotice(String imei, String imsi, String wareindex);

}
