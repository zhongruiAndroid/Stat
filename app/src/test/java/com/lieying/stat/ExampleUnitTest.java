package com.lieying.stat;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void sdf() {
        TestA testA=new TestA();
        System.out.println(testA.paramAttr.getClass().getSimpleName()+"===========");
    }
    public class TestA{
        public String paramAttr="aa";
    }
}