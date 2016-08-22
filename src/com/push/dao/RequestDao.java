package com.push.dao;

import java.util.List;

import com.push.entity.App;
import com.push.entity.Soft;

public interface RequestDao {
	public App queryAPP(String sql);

	/**
	 * 测试id请求返回数据
	 * @param wareindex
	 * @return
	 */
	public List<Soft> getPushsoft(String wareindex);


	/**
	 * 根据优先级查询产品
	 * @param code
	 * @return
	 */
	public Soft getPushsoft(Integer code,int operation);


	public List<Soft> getfour(String wareindex, int j, String hadindex, int operation);

	/**
	 * 实体广告
	 * @param priority
	 * @param operation
	 * @return
	 */
	public Soft getPushadv(int priority, int operation);

	public List<Soft> getPushadv();

	/**
	 * 黑名单推送产品
	 * @return
	 */
	public List<Soft> getBlacklistsoft();

	public int getCount(String appid);


}
