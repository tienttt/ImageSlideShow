package com.ygaps.mybackground;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tpl on 6/20/17.
 */

public class MBGResponse {
    @SerializedName("x")
    private String url;

    public boolean isEmpty(){
        if(TextUtils.isEmpty(url))
            return true;
        else
            return false;
    }

    public String getUrl() throws Exception {
        MCrypt m = new MCrypt();
        String y = new String( m.decrypt( url ) );
        return y;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
