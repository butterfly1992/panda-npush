package com.push.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.log4j.Logger;

import com.push.dao.RequestDao;
import com.push.entity.App;
import com.push.entity.Soft;
import com.push.util.Utils;

public class RequestDaoImpl implements RequestDao {
	Logger logger = Logger.getLogger("ReqDaoImpl");
	private QueryRunner qr = new QueryRunner(DbUtils.getDataSource());

	@Override
	public App queryAPP(String sql) {
		// TODO Auto-generated method stub
		App app = null;
		List<App> list = null;
		try {
			list = qr.query(sql, new BeanListHandler<App>(App.class));
			if (list.size() != 0)
				app = list.get(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return app;
	}

	/**
	 * 测试id返回请求数据
	 */
	@Override
	public List<Soft> getPushsoft(String wareindex) {
		// TODO Auto-generated method stub
		List<Soft> pushsoft = null;
		String sql = "SELECT id, NAME,pck, info1,icon,info,wareindex,apk,code,info2,img1,img2,TYPE,npway FROM "
				+ "(SELECT id, NAME, pck, icon, info1,info,npcode code,SUBSTR(pushimg,1,INSTR(pushimg,',')-1) img1,"
				+ "SUBSTR(pushimg,INSTR(pushimg,',')+1,LENGTH(pushimg)) img2,wareindex,apk, info2,"
				+ "TYPE ,npway FROM zy_soft WHERE npstatus=2   AND wareindex NOT IN(" + wareindex
				+ " )  ORDER BY npcode ASC) aa ";
		try {
			pushsoft = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("结果:" + pushsoft.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return pushsoft;
	}

	@Override
	public Soft getPushsoft(Integer code, int operation) {
		// TODO Auto-generated method stub
		List<Soft> pushsoft = null;
		String opera = "";
		if (operation == 1) {
			opera = "YDputtype softtype,IFNULL(YDprovince,'') province,";
		} else if (operation == 2) {
			opera = "LTputtype softtype,IFNULL(LTprovince,'') province,";
		} else if (operation == 3) {
			opera = "DXputtype softtype,IFNULL(DXprovince,'') province,";
		}
		String oper = "";
		if (operation > 0) {
			oper = "(FIND_IN_SET(" + operation + ",operation))  AND";
		}
		String sql = "SELECT id, NAME,pck,npcode code, info1,icon,info, " + opera
				+ " apk,wareindex,SUBSTR(pushimg,1,INSTR(pushimg,',')-1) img1,"
				+ "SUBSTR(pushimg,INSTR(pushimg,',')+1,LENGTH(pushimg)) img2,0 advsoft, info2 ,npway FROM zy_soft "
				+ "WHERE npstatus=2  AND  " + oper + "   npcode >" + code + " order by npcode limit 0,1";
		try {
			pushsoft = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("【" + sql + "】\n 结果:" + pushsoft.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (pushsoft != null && (pushsoft.size() > 0)) {
			return pushsoft.get(0);
		} else
			return null;
	}

	@Override
	public List<Soft> getfour(String wareindex, int j, String hadindex, int operation) {
		// TODO Auto-generated method stub
		List<Soft> pushsoft = null;
		String sql = "SELECT   t1.id   id,  NAME,  pck,  wareindex,icon,apk FROM zy_soft AS t1  JOIN "
				+ "(SELECT   ROUND(RAND() * ((SELECT (MAX(npcode)-7) FROM zy_soft where npstatus=2)-(SELECT MIN(npcode) FROM zy_soft where npstatus=2))+ (SELECT MIN(npcode) FROM zy_soft)) AS id) AS t2 WHERE t1.npcode >= t2. id    "
				+ "AND t1.npstatus = 2  AND wareindex NOT IN(" + wareindex + ") ORDER BY t1.npcode LIMIT 4";
		try {
			pushsoft = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("【softFour 】 result:" + pushsoft.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return pushsoft;
	}

	@Override
	public Soft getPushadv(int priority, int operation) {
		// TODO Auto-generated method stub
		List<Soft> pushsoft = null;
		String sql = "SELECT id,NAME,INFO,LINKURL,ADVINDEX,PRIORITY,logo icon,1 advsoft  FROM zy_adv "
				+ "WHERE STATUS=2 AND PRIORITY >" + priority + " ORDER BY PRIORITY LIMIT 0,1";
		try {
			pushsoft = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("【" + sql + "】\n 结果:" + pushsoft.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (pushsoft != null && (pushsoft.size() > 0)) {
			return pushsoft.get(0);
		} else
			return null;
	}

	@Override
	public List<Soft> getPushadv() {
		// TODO Auto-generated method stub
		List<Soft> pushadv = null;
		String sql = "SELECT id,NAME,INFO,LINKURL,ADVINDEX,PRIORITY,logo icon,1 advsoft  FROM zy_adv  WHERE STATUS=2    ORDER BY PRIORITY  ";
		try {
			pushadv = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("结果:" + pushadv.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return pushadv;
	}

	/**
	 * 黑名单产品，指定公司是移趣
	 */
	@Override
	public List<Soft> getBlacklistsoft() {
		// TODO Auto-generated method stub
		List<Soft> pushsoft = null;
		String sql = "SELECT id, NAME,pck, info1,icon,info,wareindex,apk,code,info2,img1,img2,TYPE,npway FROM "
				+ "(SELECT id, NAME, pck, icon, info1,info,npcode code,SUBSTR(pushimg,1,INSTR(pushimg,',')-1) img1,"
				+ "SUBSTR(pushimg,INSTR(pushimg,',')+1,LENGTH(pushimg)) img2,wareindex,apk, info2,"
				+ "TYPE ,npway FROM zy_soft WHERE npstatus=2  and company=1    ORDER BY npcode ASC) aa ";
		try {
			pushsoft = qr.query(sql, new BeanListHandler<Soft>(Soft.class));
			// Utils.log.info("结果:" + pushsoft.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return pushsoft;
	}

	@Override
	public int getCount(String appid) {
		// TODO Auto-generated method stub
		String sql = "SELECT IF(push_open_count=0,1000,push_open_count) pcount FROM zy_app 	WHERE id=?;";
		try {
			Map map = qr.query(sql, new MapHandler(), appid);
			if (map != null) {
				return Integer.parseInt(map.get("pcount").toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
