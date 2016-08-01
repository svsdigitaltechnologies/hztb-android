package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;

public class GroupDetail {
	String groupName;

	public List<UserData> getGroupMembers() {
		return groupMembers;
	}
	private List<Integer> addMembers;
	private List<Integer> deleteMembers;
	public void setGroupMembers(List<UserData> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int userId;
	List<UserData> groupMembers = new ArrayList<UserData>();
	int groupId;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	boolean isSelect;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
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
}