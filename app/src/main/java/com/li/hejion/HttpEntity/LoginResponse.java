package com.li.hejion.HttpEntity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/** 登陆回应信息
 * Created by li on 2014/7/26.
 */
public class LoginResponse extends ResponseBase{

    private final static String TAG="LoginResponse";
    private final static String SESSIONID="se";
    /**
     * SessionID
     */
    private String sessionId;

    public String getSessionId() {
        return getSessionIDfromResponse();
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * 解析json数据
     * @return
     */
    public String getSessionIDfromResponse(){
        String se="";
        if(!getDatastr().equals("")){
            try {
                JSONObject object=new JSONObject(getDatastr());
                se=object.getString(SESSIONID);
            } catch (JSONException e) {
                Log.e(TAG,e.toString());
            }
        }
        Log.e("SessionId",se);
        return se;
    }


}
