package com.li.hejion.HttpEntity;

/**
 * Created by li on 2014/7/26.
 */
public class DefaultMessageResponse<T extends  ResponseBase> {

    private T body;

    public DefaultMessageResponse(T body){
        this.body=body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
