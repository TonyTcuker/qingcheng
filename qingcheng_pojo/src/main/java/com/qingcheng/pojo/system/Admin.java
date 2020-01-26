package com.qingcheng.pojo.system;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * admin实体类
 * @author Administrator
 *
 */
@Table(name="tb_admin")
public class Admin implements Serializable{

	@Id
	/**
	 	有四种状态
	 	TABLE：使用一个特定的数据库表格来保存主键。
	 	SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。
	 	IDENTITY：主键由数据库自动生成（主要是自动增长型）
	 	AUTO：主键由程序控制。
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;//id


	private String loginName;//用户名

	private String password;//密码

	private String status;//状态

	private Date createTime;// 创建时间


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}


	
}
