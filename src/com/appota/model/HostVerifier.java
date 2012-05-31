package com.appota.model;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;

public class HostVerifier extends AbstractVerifier {

	private X509HostnameVerifier delegate;

	public HostVerifier(X509HostnameVerifier delegate) {
		this.delegate = delegate;
	}

	@Override
	public void verify(String host, String[] cns, String[] subjectAlts)
			throws SSLException {
		// TODO Auto-generated method stub
		boolean ok = false;
		try {
			delegate.verify(host, cns, subjectAlts);
		} catch (SSLException e) {
			for (String cn : cns) {
				if (cn.startsWith("*.")) {
					try {
						delegate.verify(host, new String[] { cn.substring(2) },
								subjectAlts);
						ok = true;
					} catch (Exception e1) {
					}
				}
			}
			if (!ok)
				throw e;
		}
	}

	public DefaultHttpClient getTolerantClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client
				.getConnectionManager().getSchemeRegistry().getScheme("https")
				.getSocketFactory();
		final X509HostnameVerifier delegate = sslSocketFactory
				.getHostnameVerifier();
		if (!(delegate instanceof HostVerifier)) {
			sslSocketFactory.setHostnameVerifier(new HostVerifier(delegate));
		}
		return client;
	}

}
