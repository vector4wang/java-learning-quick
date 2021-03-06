package com.design.patterns.decorator;

public class TestProgrammer extends ProgrammerDecorator {
	public TestProgrammer(Programmer decoraptor) {
		super(decoraptor);
	}

	@Override
	public void coding() {
		super.coding();
		System.out.println("testing service!");
	}
}
