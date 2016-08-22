package com.push.dao.impl;

import org.apache.commons.dbutils.QueryRunner;

import com.push.dao.InstallDao;

public class InstallDaoImpl  implements InstallDao {

	private QueryRunner qr = new QueryRunner(DbUtils.getDataSource());

	/**
	 * 统计装机量
	 */
	@Override
	public int insertAppSetup(String imei, String imsi, String appid,
			String time) throws Exception {
		// TODO Auto-generated method stub
		int i = updateAppSetup(appid, time);
		if (i == 0) {
			String sql = " INSERT INTO np_ori_appinstall "
					+ " SELECT za.name NAME,pck,1,?,username, za.id appid FROM zy_app za"
					+ " LEFT JOIN zy_developer zp ON zp.id=za.userid"
					+ "  WHERE za.id=?";
			i = qr.update(sql, time, appid);
		}
		return i;
	}

	/**
	 * 更新装机量
	 * 
	 * @param appid
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public int updateAppSetup(String appid, String time) throws Exception {
		int i = 0;
		String sql = "update np_ori_appinstall set installnum=installnum+1 where appid=? and time=?";
		i = qr.update(sql, appid, time);
		return i;
	}

}
