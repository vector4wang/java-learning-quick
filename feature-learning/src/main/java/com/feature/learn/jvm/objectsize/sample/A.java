package com.feature.learn.jvm.objectsize.sample;

/**
 * @author vector
 * @date: 2019/9/16 0016 18:29
 */
public class A {
	private int i;

	public static void main(String[] args) throws InterruptedException {
		A a = new A();
		Thread.sleep(1000 * 1000);
		System.out.println(a);
	}
}
