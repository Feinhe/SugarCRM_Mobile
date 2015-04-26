package com.sugarcrm.android.utils;

public class RIDs 
{
	public static final int MODULE_DATA = 1; 			//доступные модули
	public static final int UPCOMMING_ACTIVITES = 1<<1; //грядущие мероприятия
	public static final int ENTRY_LIST = 1<<2; 			//разделенные списки(по пагинации) каждого модуля
	public static final int SEARCH_ENTRY_LIST = 1<<3; 	//возвращаемый при поиске (находим по строке запроса - 2map)
	public static final int HOME_ENTRY_LIST = 1<<4;		//ентрилст для домашней странички
	public static final int CARD_FORM_ENTRY = 1<<5;		//инфа в карточке
	public static final int MODULES_FIELDS = 1<<6; 		//поля каждого модуля
	public static final int MODULES_LAYOUT = 1<<7; 		//лейаут каждого модуля
	public static final int LAYOUT_FIELDS = 1<<8; 		//поля лейаута каждого модуля(для удобной выборки)
	public static final int LANGUAGE_DEFINITION = 1<<9; //локализация полей
	
	public static int getRequestsNumber() {
		return RIDs.class.getFields().length;
	}
}
