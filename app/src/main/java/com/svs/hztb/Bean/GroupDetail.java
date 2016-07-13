package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;

public class GroupDetail {
	String groupName;

	public List<UserData> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(List<UserData> groupMembers) {
		this.groupMembers = groupMembers;
	}

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
}