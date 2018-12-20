import com.atlassian.jira.component.ComponentAccessor

if (getActionName() != "Create") {
	return
}

def groupsAssigneeId = "customfield_10205";
def groupAssignee = "team";
def group = ComponentAccessor.getGroupManager().getGroup(groupAssignee);

getFieldById(groupsAssigneeId).setFormValue("team");
