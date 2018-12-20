// https://jira.barceloviajes.com/browse/CIS-1488

import org.apache.log4j.Logger
def log = Logger.getLogger("com.avoristravel.cis.syncGroupAssignee.groovy")

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.ModifiedValue;

def checkIssues(Issue orig, Issue dest) {
	if(dest.getKey().equals(orig.getKey())) {
		log.error "Origen and destination issues are the same";
	}
}

String groupAssigneeFieldId = "customfield_10205";
String productResponsibleFieldId = "customfield_12002";

def groupAssigneeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(groupAssigneeFieldId);
def productResponsibleField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(productResponsibleFieldId);
def priority
//Issue issue = ComponentAccessor.getIssueManager().getIssueObject('SOP-6092');
//Issue issue = ComponentAccessor.getIssueManager().getIssueObject('SOPDES-1905');
Issue issue = event.issue as Issue;
String currentGroupAssignee, currentProductResponsible;
List<Issue> destinations = [];


// Get current value
def issueLinkManager = ComponentAccessor.getIssueLinkManager();
def custom = (List<ImmutableGroup>)issue.getCustomFieldValue(groupAssigneeField);
def customProductResponsible = (List<ImmutableGroup>)issue.getCustomFieldValue(groupAssigneeField);
custom.each { v ->
	currentGroupAssignee = ((ImmutableGroup)v).getName();
}
def customProduct = (List<ImmutableGroup>)issue.getCustomFieldValue(productResponsibleField);
customProduct.each { v ->
	currentProductResponsible = ((ImmutableGroup)v).getName();
}


// Get links
issueLinkManager.getInwardLinks(issue.id).each { l -> // is cloned by
	if("is cloned by".equals(l.getIssueLinkType().getInward())) {
		Issue dest = l.getSourceObject();
		checkIssues(issue, dest);
		destinations.add(l.getSourceObject());
	}
}

issueLinkManager.getOutwardLinks(issue.id).each { l -> // clones
	if("clones".equals(l.getIssueLinkType().getOutward())) {
		Issue dest = l.getDestinationObject();
		checkIssues(issue, dest);
		destinations.add(l.getDestinationObject());
	}
}


// Destination object
for(Issue des : destinations) {
		String destinationGroupAssignee, destinationProductResponsible;
		def gaf = (List<ImmutableGroup>)des.getCustomFieldValue(groupAssigneeField);
		gaf.each { v ->
			destinationGroupAssignee = ((ImmutableGroup)v).getName();
			if(currentGroupAssignee != destinationGroupAssignee) {
				def target = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects(des).find { it.id == groupAssigneeFieldId };
				def changeHolder = new DefaultIssueChangeHolder();
				target.updateValue(null, des, new ModifiedValue(des.getCustomFieldValue(target), issue.getCustomFieldValue(groupAssigneeField)), changeHolder);
			}
		}
        def gafProductResponsible = (List<ImmutableGroup>)des.getCustomFieldValue(productResponsibleField);
		gafProductResponsible.each { v ->
			destinationProductResponsible = ((ImmutableGroup)v).getName();
			if(currentProductResponsible != destinationProductResponsible) {
				def target = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects(des).find { it.id == productResponsibleFieldId };
				def changeHolder = new DefaultIssueChangeHolder();
				target.updateValue(null, des, new ModifiedValue(des.getCustomFieldValue(target), issue.getCustomFieldValue(productResponsibleField)), changeHolder);
			}
		}
}

