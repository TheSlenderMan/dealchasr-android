package uk.co.almanacmedia.dealchasr.dealchasr;

/**
 * Created by Sam on 23/02/2018.
 */

public class InterestsRecyclerModel {

    private String vName;
    private String dName;
    private String vDesc;
    private String time;
    private String venName;
    private Integer rec;
    private Integer ID;
    private Integer daily;

    public InterestsRecyclerModel(Integer ID, String voucherName, String dealName, String dealDesc,
                                String venueName, String dealDate, Integer recurring, Integer daily) {
        this.vName = voucherName;
        this.dName = dealName;
        this.vDesc = dealDesc;
        this.time = dealDate;
        this.venName = venueName;
        this.rec = recurring;
        this.ID = ID;
        this.daily = daily;
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

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        time = time;
    }

    public String getVenueName() {
        return venName;
    }
    public void setVenueName(String venName) {
        venName = venName;
    }

    public String getDealDate() {
        return time;
    }
    public void setDealDate(String time) {
        time = time;
    }

    public Integer getRecurring() {
        return rec;
    }
    public void setRecurring(Integer rec) {
        rec = rec;
    }

    public Integer getID() {
        return ID;
    }
    public void setID(Integer ID) {
        ID = ID;
    }

    public Integer getDaily() {
        return daily;
    }
    public void setDaily(Integer daily) {
        daily = daily;
    }
}
