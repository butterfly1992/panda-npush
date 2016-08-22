package com.push.Service.impl;

import com.push.dao.InstallDao;

public class InstallServiceImpl {
	private InstallDao installDao = DaoFactory.getInstance(
			"com.push.dao.impl.InstallDaoImpl", InstallDao.class);

	public int appInstall(String imei, String imsi, String appid, String time,
			String gysdkv) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			result = installDao.insertAppSetup(imei, imsi, appid, time);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 装机量统计
		return result;
	}
}
