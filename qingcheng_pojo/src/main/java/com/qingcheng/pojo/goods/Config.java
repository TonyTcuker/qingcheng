package com.qingcheng.pojo.goods;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * config实体类
 * @author Administrator
 *
 */
@Table(name="sys_config")
public class Config implements Serializable{

	@Id
	private String variable;//variable


	

	private String value;//value

	private java.util.Date setTime;//set_time

	private String setBy;//set_by

	
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public java.util.Date getSetTime() {
		return setTime;
	}
	public void setSetTime(java.util.Date setTime) {
		this.setTime = setTime;
	}

	public String getSetBy() {
		return setBy;
	}
	public void setSetBy(String setBy) {
		this.setBy = setBy;
	}


	
}
