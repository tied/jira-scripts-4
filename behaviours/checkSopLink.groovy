import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.link.IssueLink;
import com.opensymphony.workflow.InvalidInputException
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.onresolve.scriptrunner.runner.util.UserMessageUtil
import com.atlassian.jira.issue.Issue

def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)
def linkedIssuesCF = getFieldById('issuelinks')
def linkedIssues = getFieldById("issuelinks-issues").toString()
log.debug("linkedIssuesCF: " + linkedIssues)
if(!linkedIssues.contains("SOP-")){
	log.debug("No se ha especficado una SOP enlazada")
	linkedIssuesCF.setError("No se ha especficado una SOP enlazada")
}else{
    linkedIssuesCF.clearError()
}