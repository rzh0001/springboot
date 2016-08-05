package com.smily.mybatis;

public abstract interface ArgumentsAware {
	public abstract void setArguments(String[] paramArrayOfString) throws Exception;
}