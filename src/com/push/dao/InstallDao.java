package com.push.dao;

public interface InstallDao {
	public int insertAppSetup(String imei, String imsi, String appid,
			String time) throws Exception;

}
