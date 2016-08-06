package com.svs.hztb.Bean;

import java.util.List;

public class GroupInput {
	private int groupId;
	private int userId;
	
	private String groupName;
	private String groupDesc;
	private String newGroupName;
	private List<Integer> addMembers;
	private List<Integer> deleteMembers;


	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public List<Integer> getAddMembers() {
		return addMembers;
	}
	public void setAddMembers(List<Integer> addMembers) {
		this.addMembers = addMembers;
	}
	public List<Integer> getDeleteMembers() {
		return deleteMembers;
	}
	public void setDeleteMembers(List<Integer> deleteMembers) {
		this.deleteMembers = deleteMembers;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupDesc() {
		return groupDesc;
	}
	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}
	public String getNewGroupName() {
		return newGroupName;
	}
	public void setNewGroupName(String newGroupName) {
		int i =0;
		this.newGroupName = newGroupName;
	}
	
	
}