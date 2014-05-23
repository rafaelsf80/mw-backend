package backend;

import java.util.Date;

/**
 * The object model for the data we are sending through endpoints
 */
public class CaseBean {

    private long id;
    private String caseTitle;
    private String caseOwner;
    private Date caseCreated;
    private Date caseClosed;



    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getCaseTitle() {
        return caseTitle;
    }
    public void setCaseTitle(String data) {
        caseTitle = data;
    }

    public String getCaseOwner() {
        return caseOwner;
    }

    public void setCaseOwner(String caseOwner) {
        this.caseOwner = caseOwner;
    }

    public Date getCaseCreated() {
        return caseCreated;
    }

    public void setCaseCreated(Date caseCreated) {
        this.caseCreated = caseCreated;
    }

    public Date getCaseClosed() {
        return caseClosed;
    }

    public void setCaseClosed(Date caseClosed) {
        this.caseClosed = caseClosed;
    }
}