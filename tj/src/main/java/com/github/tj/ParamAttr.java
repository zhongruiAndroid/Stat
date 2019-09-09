package com.github.tj;

import java.io.Serializable;

/***
 *   created by android on 2019/9/6
 */
public class ParamAttr implements Serializable {
    /**
     * 书籍id
     */
    public String book_id;
    /***
     * 章节id
     */
    public String chapter_id;

    @Override
    public String toString() {
        return "ParamAttr{" +
                "book_id='" + book_id + '\'' +
                ", chapter_id='" + chapter_id + '\'' +
                '}';
    }
}
