import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.link.IssueLinkType
import com.atlassian.jira.issue.link.IssueLinkTypeManager

Issue issue = issue;
IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager);
IssueLinkManager issueLinkManager = ComponentAccessor.issueLinkManager;
Collection<IssueLinkType> storyLinkTypes = issueLinkTypeManager.getIssueLinkTypesByName('Epic-Story Link');

if (issue.issueType.name == "Epica"){
	if (storyLinkTypes) {
		Long storyLinkTypeId = storyLinkTypes[0].id;
		def linkedStories = issueLinkManager.getOutwardLinks(issue.id).findAll{it.linkTypeId==storyLinkTypeId}*.destinationObject;
	
		java.lang.Number numStories = linkedStories?.size()?:0
		if (numStories>0) {
			java.lang.Number numClosedStories = linkedStories?.count{it?.status?.name.equalsIgnoreCase('HECHA') || it?.status?.name.equalsIgnoreCase('DESCARTADA')}?:0
			if(numClosedStories != numStories){
					return false;
			}
		}
		
		return true;
	}    
}
