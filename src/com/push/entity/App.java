package com.push.entity;


public class App {
    private String id;
    private String userid;
    private String name;
    private String pck;
    private String type;
    private String icon;
    private String description;
    private String apk;
    private Integer status;
    private String createtime;
    private String releasetime;
    private Integer push_open_status;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserid() {
        return userid;
    }
    
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPck() {
        return pck;
    }
    
    public void setPck(String pck) {
        this.pck = pck;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getApk() {
        return apk;
    }
    
    public void setApk(String apk) {
        this.apk = apk;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    
    
    public String getCreatetime() {
        return createtime;
    }
    
    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
    
    public String getReleasetime() {
        return releasetime;
    }
    
    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

	public Integer getPush_open_status() {
		return push_open_status;
	}

	public void setPush_open_status(Integer push_open_status) {
		this.push_open_status = push_open_status;
	}
    
     
}
