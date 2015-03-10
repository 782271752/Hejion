package com.li.hejion.HttpEntity;

import java.io.Serializable;

/**
 * Created by li on 2014/7/26.
 */
public class ResponseBase implements Serializable{
    private String errorcode;
    private String massage;
    private String datastr;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getDatastr() {
        return datastr;
    }

    public void setDatastr(String datastr) {
        this.datastr = datastr;
    }
}
