package com.sugarcrm.android.database;

import com.google.gson.JsonElement;

public class DBUtils 
{
	/*
	 * формируем запрос на создание таблицы с заданным именем и колоками
	 */
	public static String getTableCreateString(String tableName, String[] columNames) {
		StringBuilder result = new StringBuilder("create table "+ tableName+" (");
		result.append(columNames[0]+" integer primary key autoincrement");
		for(int i=1; i<columNames.length; i++) {
			result.append(","+columNames[i]+" text");
		} result.append(");");
		
		return result.toString();
	}
}
