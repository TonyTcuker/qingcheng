package com.qingcheng.pojo.system;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * role实体类
 * @author Administrator
 *
 */
@Table(name="tb_role")
public class Role implements Serializable{

	@Id
	private Integer id;//ID

	private String name;//角色名称

	private String status; // 状态
	private String detail; // 角色描述
	private Date createTime; // 创建时间

	@Transient // 表示 非数据库表中字段
	private Integer countAdmin ;// 统计拥有此角色的管理员，在数据表中没有此字段

	public Integer getCountAdmin() {
		return countAdmin;
	}

	public void setCountAdmin(Integer countAdmin) {
		this.countAdmin = countAdmin;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	
}
