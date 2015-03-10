package com.li.hejion.HttpEntity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by li on 2014/7/28.
 */
public class UserInfoResponse extends ResponseBase{

    private final static String TAG="UserInfoResponse";

    /**
     * 用户编号
     */
    private final static String USER_ID="userid";
    /**
     * 头像地址
     */
    private final static String HEAD_IMG="headimg";
    /**
     * 昵称
     */
    private final static String NICK_NAME="nickname";
    /**
     * 性别0未知1男2女
     */
    private final static String SEX="sex";
    /**
     * 所在地省市
     */
    private final static String AREA="area";
    /**
     * 自我介绍
     */
    private final static String INTRODUCTION="introduction";
    /**
     * 威望（暂不用)
     */
    private final static String ACHIVE="achive";
    /**
     * 余款
     */
    private final static String BALANCE="balance";
    /**
     * 积分
     */
    private final static String SCORE="score";




    public static class UserInfo{
        private String useid;
        private String headimg;
        private String nickname;
        private String sex;
        private String area;
        private String introduction;
        private String achive;
        private String balance;
        private String score;

        public String getUseid() {
            return useid;
        }

        public void setUseid(String useid) {
            this.useid = useid;
        }

        public String getHeading() {
            return headimg;
        }

        public void setHeading(String heading) {
            this.headimg = heading;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getAchive() {
            return achive;
        }

        public void setAchive(String achive) {
            this.achive = achive;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }

    public UserInfo getUserInfoResult(){
        UserInfo userInfo=new UserInfo();
        try {
            JSONObject object=new JSONObject(getDatastr());
            userInfo.setUseid(object.getString(USER_ID));
            userInfo.setHeading(object.getString(HEAD_IMG));
            userInfo.setNickname(object.getString(NICK_NAME));
            userInfo.setSex(object.getString(SEX));
            userInfo.setArea(object.getString(AREA));
            userInfo.setIntroduction(object.getString(INTRODUCTION));
            userInfo.setAchive(object.getString(ACHIVE));
            userInfo.setBalance(object.getString(BALANCE));
            userInfo.setScore(object.getString(SCORE));
        } catch (JSONException e) {
            Log.e(TAG,e.toString());
        }

        return userInfo;
    }
}
