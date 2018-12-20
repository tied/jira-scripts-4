import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.link.IssueLinkManager
	 
// Allow logging for debug and tracking purposes
import org.apache.log4j.Level
import org.apache.log4j.Logger
	 
// Script code for easy log identification
String scriptCode = "Check all stories closed -"
	 
// Setup the log and leave a message to show what we're doing
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)
log.debug( "$scriptCode Triggered by $issue.key" )
	
passesCondition = true
if (issue.issueType.name == 'Epica')
   {
     IssueLinkManager issueLinkManager = ComponentAccessor.issueLinkManager
     def found = issueLinkManager.getOutwardLinks(issue.id).any
       {
        it?.IssueLinkType?.getName() == 'Epic-Story Link' && it?.destinationObject?.getStatus().getName() != 'HECHA' && it?.destinationObject?.getStatus().getName() != 'DESCARTADA' && it?.destinationObject?.getStatus().getName() != 'RACHAZADA' && it?.destinationObject?.getStatus().getName() != 'RESUELTA' && it?.destinationObject?.getStatus().getName() != 'DESCARTADO'
       }
       log.debug( "$scriptCode Found =  $found " )
       if (found) {
           log.debug( "$scriptCode return false" )
           passesCondition = false
       } else {
           log.debug( "$scriptCode return true" )
	   passesCondition = true
       }
   }
// Always allow all other issue types to execute this transition
   else
   {
       log.debug( "$scriptCode Not Epic return true" )
       passesCondition = true
   }
