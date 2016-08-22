/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.push.util;


import org.apache.commons.dbutils.QueryRunner;

import com.push.dao.impl.DbUtils;

/**
 * 
 * @author Administrator
 */
public class GeneralDB {
	private QueryRunner qr = new QueryRunner(DbUtils.getDataSource());

	public int update(String sql) {
		try {
			return qr.update(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
}
