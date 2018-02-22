package uk.co.almanacmedia.dealchasr.dealchasr;

/**
 * Created by Sam on 23/02/2018.
 */

public class VoucherRecyclerModel {

    private String vName;
    private String dName;
    private String vDesc;
    private Integer ID;
    private String time;
    private String venName;
    private String vCount;

    public VoucherRecyclerModel(String voucherName, String dealName, String dealDesc, Integer ID, String time,
                         String venueName, String voucherCount) {
        this.vName = voucherName;
        this.dName = dealName;
        this.vDesc = dealDesc;
        this.ID = ID;
        this.time = time;
        this.venName = venueName;
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

    public String getVenueName() {
        return venName;
    }
    public void setVenueName(Integer venName) {
        venName = venName;
    }

    public String getVoucherCount() {
        return vCount;
    }
    public void setVoucherCount(String vCount) {
        vCount = vCount;
    }
}
