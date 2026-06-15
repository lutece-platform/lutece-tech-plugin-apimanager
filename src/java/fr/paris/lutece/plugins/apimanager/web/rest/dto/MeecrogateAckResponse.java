package fr.paris.lutece.plugins.apimanager.web.rest.dto;

public class MeecrogateAckResponse {
    public String updateCommitId;
    public String updateDate;
    public String deployGatewayStatus;
    public String deployOauth2Status;

    public String getUpdateCommitId() {
        return updateCommitId;
    }

    public void setUpdateCommitId(String updateCommitId) {
        this.updateCommitId = updateCommitId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDeployGatewayStatus() {
        return deployGatewayStatus;
    }

    public void setDeployGatewayStatus(String deployGatewayStatus) {
        this.deployGatewayStatus = deployGatewayStatus;
    }

    public String getDeployOauth2Status() {
        return deployOauth2Status;
    }

    public void setDeployOauth2Status(String deployOauth2Status) {
        this.deployOauth2Status = deployOauth2Status;
    }
}
