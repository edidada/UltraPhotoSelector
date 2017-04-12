package com.ultra.photoselector.controller;

/**
 * 
 * @author 
 *
 */
public class SelectNumberController {

	private SelectNumberController(){
		
	}
	
	private static SelectNumberController ins;

	public static SelectNumberController getInstance(){
		if(ins == null){
			ins = new SelectNumberController();
		}
		return ins;
	}
	
	private boolean getIoff = true;
	
	private int num;

	private int MaxNum;

	private int MaxNumTemp;

	public static SelectNumberController getIns() {
		return ins;
	}

	public static void setIns(SelectNumberController ins) {
		SelectNumberController.ins = ins;
	}

	public boolean isGetIoff() {
		return getIoff;
	}

	public void setGetIoff(boolean getIoff) {
		this.getIoff = getIoff;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getMaxNum() {
		return MaxNum;
	}

	public void setMaxNum(int maxNum) {
		MaxNum = maxNum;
	}

	public int getMaxNumTemp() {
		return MaxNumTemp;
	}

	public void setMaxNumTemp(int maxNumTemp) {
		MaxNumTemp = maxNumTemp;
	}
	
}
