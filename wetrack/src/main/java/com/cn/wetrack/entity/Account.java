package com.cn.wetrack.entity;

/**
 * 账户信息
 */
public class Account {
	/* 用户昵称 */
	private String NiceName;
	/* 公司名称 */
	private String CompanyName;
	/* 用户类型 */
	private int Grade;
	/* 联系地址 */
	private String Address;
	/* 联系人 */
	private String LinkMan;
	/* 联系电话 */
	private String Contact;
	/* 备注 */
	private String Remark;
	/*公司地址*/
	private String CompanyAddress;
	public String getCompanyAddress() {
		return CompanyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		CompanyAddress = companyAddress;
	}

	public String getNiceName() {
		return NiceName;
	}

	public void setNiceName(String niceName) {
		NiceName = niceName;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public int getGrade() {
		return Grade;
	}

	public void setGrade(int grade) {
		Grade = grade;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getLinkMan() {
		return LinkMan;
	}

	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}

	public String getContact() {
		return Contact;
	}

	public void setContact(String contact) {
		Contact = contact;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

}
