package com.design.patterns.dependency_inversion_principle.before;

/**
 * @Author: wangxc
 * @GitHub: https://github.com/vector4wang
 * @CSDN: http://blog.csdn.net/qqhjqs?viewmode=contents
 * @BLOG: http://vector4wang.tk
 * @wxid: BMHJQS
 */
public class TestMain {

    public static void main(String[] args) {

        Zoo zoo = new Zoo("dog");
        zoo.AnimalEat();
        zoo.AnimalRun();
    }
}

