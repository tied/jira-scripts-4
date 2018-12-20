import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.crowd.embedded.impl.ImmutableGroup
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
  
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)

def isInGroup
try{
    CustomField cf_groupAproval = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_11700")
    def groupAprovalValue = ((List<ImmutableGroup>) issue.getCustomFieldValue(cf_groupAproval)).get(0).getName().toString()
    def groupAproval = ComponentAccessor.groupManager.getGroup(groupAprovalValue);
    def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    
    isInGroup = ComponentAccessor.groupManager.getUserNamesInGroup(groupAproval).contains(currentUser.getName());
    log.debug "isInGroup:"+isInGroup
    }catch(Exception e){
    return true
	}
    if(isInGroup == false)
         throw new InvalidInputException("You are not authorized to aprove this issue")

