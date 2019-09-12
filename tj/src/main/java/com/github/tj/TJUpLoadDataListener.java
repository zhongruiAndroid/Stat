package com.github.tj;

import java.io.Serializable;
import java.util.List;

/***
 *   created by android on 2019/9/4
 */
public interface TJUpLoadDataListener extends Serializable {
    //页面上报
    void uploadPageData(List<PageBean> list);
    //广告点击上报
    void uploadAdvertClickData(List<AdvertUploadBean> list);
    //其他点击上报
    void uploadOtherClickData(List<AdvertUploadBean> list);
}
