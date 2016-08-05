package com.smily.mybatis;

import org.apache.commons.vfs2.util.Cryptor;
import org.apache.commons.vfs2.util.CryptorFactory;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class Decryptor implements InitializingBean {
	public static final String START_MARK = "{";
	public static final String END_MARK = "}";
	private Cryptor cryptor;

	public Cryptor getCryptor() {
		return cryptor;
	}

	@Required
	public void setCryptor(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	@Override
	public void afterPropertiesSet() {
		CryptorFactory.setCryptor(cryptor);
	}

	public String decrypt(String password) throws Exception {
		return decrypt(cryptor, password);
	}

	@Contract("_, null -> null")
	public static String decrypt(Cryptor cryptor, String password) throws Exception {
		if (password == null) {
			return null;
		}

		if ((password.startsWith("{")) && (password.endsWith("}"))) {
			return cryptor.decrypt(password.substring(1, password.length() - 1));
		}

		return password;
	}
}
