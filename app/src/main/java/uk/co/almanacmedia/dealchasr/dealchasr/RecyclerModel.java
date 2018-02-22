package uk.co.almanacmedia.dealchasr.dealchasr;

/**
 * Created by Sam on 19/02/2018.
 */

public class RecyclerModel {

    private String vName;
    private String dName;
    private String vDesc;
    private String type;
    private Integer ID;
    private String time;
    private Integer rec;
    private String venName;
    private String venWeb;
    private Integer venID;
    private Boolean status;
    private String vCount;

    public RecyclerModel(String voucherName, String dealName, String dealDesc, String type, Integer ID, String time, Integer rec,
                         String venueName, String venWeb, Integer venID, Boolean status, String voucherCount) {
        this.vName = voucherName;
        this.dName = dealName;
        this.vDesc = dealDesc;
        this.type = type;
        this.ID = ID;
        this.time = time;
        this.rec = rec;
        this.venName = venueName;
        this.venWeb = venWeb;
        this.venID = venID;
        this.status = status;
        this.vCount = voucherCount;
    }
    public String getDealName() {
        return dName;
    }
    public void setSunName(String dName) {
        dName = dName;
    }

    public String getVoucherName() {
        return vName;
    }
    public void setName(String vName) {
        vName = vName;
    }

    public String getDescriptionName() {
        return vDesc;
    }
    public void setDescription(String vDesc) {
        vDesc = vDesc;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        type = type;
    }

    public Integer getID() {
        return ID;
    }
    public void setID(String ID) {
        ID = ID;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        time = time;
    }

    public Integer getRecurring() {
        return rec;
    }
    public void setRecurring(Integer rec) {
        rec = rec;
    }

    public String getVenueName() {
        return venName;
    }
    public void setVenueName(Integer venName) {
        venName = venName;
    }

    public String getVenueWeb() {
        return venWeb;
    }
    public void setVenueWeb(String venWeb) {
        venWeb = venWeb;
    }

    public Integer getVenueID() {
        return venID;
    }
    public void setVenueID(Integer venID) {
        venID = venID;
    }

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        status = status;
    }

    public String getVoucherCount() {
        return vCount;
    }
    public void setVoucherCount(String vCount) {
        vCount = vCount;
    }
}
