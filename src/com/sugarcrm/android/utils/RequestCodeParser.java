package com.sugarcrm.android.utils;

import java.util.ArrayList;

public class RequestCodeParser 
{
	public static ArrayList<Integer> getCodeEnum(int requestCode) {
		ArrayList<Integer> codeEnum = new ArrayList<Integer>();
		int rn = (int) Math.pow(2, RIDs.getRequestsNumber()); //max request value (<=32)
		
		int mask = 1;
		while(mask != rn) {
			int v = requestCode&mask;
			if(v>0) {
				codeEnum.add(v);
			} mask = mask<<1;
		} codeEnum.trimToSize();
		
		return codeEnum;
	}
}
