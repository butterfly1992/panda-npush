package com.push.entity;

import java.io.Serializable;

public class Soft implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;// 产品id
	private String name;// 产品名称
	private int advsoft;// 默认产品广告，1是实体广告

	/* 产品广告属性 */

	private String info;// 产品诱惑标题
	private String info1;// 产品短简介
	private String info2;// 产品长简介
	private String pck;// 产品包名
	private int code;// 产品优先级
	private int wareindex;// 产品索引
	private String icon;// logo
	private String apk;// apk
	private String img1;
	private String img2;
	
	private int softtype;
	private String province;
	
	/*升级功能，通知后触发方式*/
	private int npway;
	

	/* 下方是实体广告属性 */
	private String linkurl;
	private int advindex;
	private int priority;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getPck() {
		return pck;
	}

	public void setPck(String pck) {
		this.pck = pck;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getWareindex() {
		return wareindex;
	}

	public void setWareindex(int wareindex) {
		this.wareindex = wareindex;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}


	public int getAdvindex() {
		return advindex;
	}

	public void setAdvindex(int advindex) {
		this.advindex = advindex;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getAdvsoft() {
		return advsoft;
	}

	public void setAdvsoft(int advsoft) {
		this.advsoft = advsoft;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getApk() {
		return apk;
	}

	public void setApk(String apk) {
		this.apk = apk;
	}

	public String getImg1() {
		return img1;
	}

	public void setImg1(String img1) {
		this.img1 = img1;
	}

	public String getImg2() {
		return img2;
	}

	public void setImg2(String img2) {
		this.img2 = img2;
	}

	public int getSofttype() {
		return softtype;
	}

	public void setSofttype(int softtype) {
		this.softtype = softtype;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public int getNpway() {
		return npway;
	}

	public void setNpway(int npway) {
		this.npway = npway;
	}

	
}
