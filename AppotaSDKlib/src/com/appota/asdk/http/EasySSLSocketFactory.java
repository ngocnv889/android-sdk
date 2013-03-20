package com.appota.asdk.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Build;

import com.appota.asdk.R;

public class EasySSLSocketFactory implements SocketFactory,
		LayeredSocketFactory {

	private SSLContext sslcontext = null;
	private static Context ctx;

	public EasySSLSocketFactory(Context context) {
		EasySSLSocketFactory.ctx = context;
	}

	private static SSLContext createEasySSLContext() {
		SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLS");
			if (Build.VERSION.SDK_INT >= 14) {
				KeyStore trusted = KeyStore.getInstance("BKS");
				InputStream in = ctx.getResources().openRawResource(R.raw.asvnkeystore);
				try {
					trusted.load(in, "123465".toCharArray());
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				context.init(null,new TrustManager[] { new EasyX509TrustManager(trusted) },null);
			} else {
				context.init(null,new TrustManager[] { new EasyX509TrustManager(null) },null);
			}
			return context;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return context;
	}

	private SSLContext getSSLContext() throws IOException {
		if (this.sslcontext == null) {
			this.sslcontext = createEasySSLContext();
		}
		return this.sslcontext;
	}

	@Override
	public Socket createSocket(Socket sock, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return getSSLContext().getSocketFactory().createSocket(sock, host,
				port, autoClose);
	}

	@Override
	public Socket connectSocket(Socket sock, String host, int port,
			InetAddress localAddress, int localPort, HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		// TODO Auto-generated method stub
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);
		InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

		if ((localAddress != null) || (localPort > 0)) {
			// we need to bind explicitly
			if (localPort < 0) {
				localPort = 0; // indicates "any"
			}
			InetSocketAddress isa = new InetSocketAddress(localAddress,
					localPort);
			sslsock.bind(isa);
		}

		sslsock.connect(remoteAddress, connTimeout);
		sslsock.setSoTimeout(soTimeout);
		return sslsock;
	}

	@Override
	public Socket createSocket() throws IOException {
		// TODO Auto-generated method stub
		return getSSLContext().getSocketFactory().createSocket();
	}

	@Override
	public boolean isSecure(Socket arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean equals(Object obj) {
		return ((obj != null) && obj.getClass().equals(
				EasySSLSocketFactory.class));
	}

	public int hashCode() {
		return EasySSLSocketFactory.class.hashCode();
	}

}
