import static com.atlassian.jira.issue.IssueFieldConstants.*

def cfComponent = getFieldById(COMPONENTS);
def compValue = cfComponent.getFormValue()

if (compValue instanceof List) {
cfComponent.setError("Please select only ONE component");
} else {
cfComponent.clearError();
}