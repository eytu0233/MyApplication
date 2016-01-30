package edu.ncku.application.util;

/**
 * 完成登入工作後，呼叫此介面方法來通知結果
 */
public interface ILoginResultListener {

	public void loginEvent(boolean login);
	
}
