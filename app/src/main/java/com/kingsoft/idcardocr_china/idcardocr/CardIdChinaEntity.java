package com.kingsoft.idcardocr_china.idcardocr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangyangke on 2017/4/24.
 */

public class CardIdChinaEntity implements Parcelable {
    /**
     * 姓名
     */
    private String name="";
    /**
     * 姓别
     */
    private String sex="";
    /**
     * 民族
     */
    private String nation="";
    /**
     * 出生年月日-年
     */
    private String year="";
    /**
     * 出生年月日-月
     */
    private String month="";
    /**
     * 出生年月日-日
     */
    private String day="";
    /**
     * 地址
     */
    private String address="";
    /**
     * 身份证号
     */
    private String idCardNo="";
    /**
     * 签发机关
     */
    private String issuingAuthority="";
    /**
     * 有限期限
     */
    private String expiryDate="";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.sex);
        dest.writeString(this.nation);
        dest.writeString(this.year);
        dest.writeString(this.month);
        dest.writeString(this.day);
        dest.writeString(this.address);
        dest.writeString(this.idCardNo);
        dest.writeString(this.issuingAuthority);
        dest.writeString(this.expiryDate);
    }

    public CardIdChinaEntity() {
    }

    protected CardIdChinaEntity(Parcel in) {
        this.name = in.readString();
        this.sex = in.readString();
        this.nation = in.readString();
        this.year = in.readString();
        this.month = in.readString();
        this.day = in.readString();
        this.address = in.readString();
        this.idCardNo = in.readString();
        this.issuingAuthority = in.readString();
        this.expiryDate = in.readString();
    }

    public static final Parcelable.Creator<CardIdChinaEntity> CREATOR = new Parcelable.Creator<CardIdChinaEntity>() {
        @Override
        public CardIdChinaEntity createFromParcel(Parcel source) {
            return new CardIdChinaEntity(source);
        }

        @Override
        public CardIdChinaEntity[] newArray(int size) {
            return new CardIdChinaEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
