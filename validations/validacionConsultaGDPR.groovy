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
    def equipoDisenoGDPR = "SOMETEAM"
    def equipoQAGDPR = "SOMETEAM"
    def equipoAprobador
    
	CustomField groupAssigneeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10205")
    def groupAssigneeValue = ((List<ImmutableGroup>) issue.getCustomFieldValue(groupAssigneeField)).get(0).getName().toString()
    
    if(groupAssigneeValue.equals("SOMETEAM"))
    	equipoAprobador = equipoQAGDPR
    else if(groupAssigneeValue.contains("SOMETEAM"))
        equipoAprobador = equipoDisenoGDPR
    else
        return true
        
	def equipoAprobadorField = ComponentAccessor.groupManager.getGroup(equipoAprobador)
    def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    
    isInGroup = ComponentAccessor.groupManager.getUserNamesInGroup(equipoAprobadorField).contains(currentUser.getName())
    log.debug "isInGroup:"+isInGroup
    }catch(Exception e){
    return true
	}
    if(isInGroup == false)
         throw new InvalidInputException("No estas autorizado para aprobar esta tarea")
