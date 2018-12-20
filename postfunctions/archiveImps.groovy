import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.IssueInputParametersImpl

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
IssueService issueService = ComponentAccessor.getIssueService()

def issueComponent = issue.getComponents().getAt(0)?.getName()
def ownKey = issue.getKey()
def query = jqlQueryParser.parseQuery("project = PROJECT NAME AND status in ('SOMESTATUS') AND component = " + issueComponent + " AND issuekey != " + ownKey)
def actionId = 51
def transitionValidationResult
def transitionResult

def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())

results.getIssues().each {documentIssue ->
    def issue = issueManager.getIssueObject(documentIssue.id)
     transitionValidationResult = issueService.validateTransition(user, issue.id, actionId,new IssueInputParametersImpl())
	 if (transitionValidationResult.isValid()) {
 		transitionResult = issueService.transition(user, transitionValidationResult)
	}
}