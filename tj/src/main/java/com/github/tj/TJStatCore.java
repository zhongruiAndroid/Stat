package com.github.tj;

import android.app.Activity;

/***
 *   created by android on 2019/8/29
 */
public class TJStatCore {
    private static TJStatCore singleObj;

    private TJStatCore() {
    }

    public static TJStatCore get() {
        if (singleObj == null) {
            synchronized (TJStatCore.class) {
                if (singleObj == null) {
                    singleObj = new TJStatCore();
                }
            }
        }
        return singleObj;
    }

    private Activity topAct;

    public Activity getTopAct() {
        return topAct;
    }

    public void setTopAct(Activity topAct) {
        this.topAct = topAct;
    }
}
