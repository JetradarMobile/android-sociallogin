package com.jetradarmobile.sociallogin.mailru

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

internal class Tls12SocketFactory(
    private val delegate: SSLSocketFactory = SSLContext.getInstance("TLSv1.2").apply { init(null, null, null) }.socketFactory
) : SSLSocketFactory() {
  override fun getDefaultCipherSuites(): Array<String> = delegate.defaultCipherSuites
  override fun getSupportedCipherSuites(): Array<String> = delegate.supportedCipherSuites

  override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket = delegate.createSocket(s, host, port, autoClose).patch()
  override fun createSocket(host: String?, port: Int): Socket = delegate.createSocket(host, port).patch()
  override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket = delegate.createSocket(host, port, localHost, localPort).patch()
  override fun createSocket(host: InetAddress?, port: Int): Socket = delegate.createSocket(host, port).patch()
  override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket = delegate.createSocket(address, port, localAddress, localPort).patch()

  private fun Socket.patch() = apply { (this as? SSLSocket)?.enabledProtocols = TLS_V12_ONLY }

  private companion object {
    private val TLS_V12_ONLY = arrayOf("TLSv1.1", "TLSv1.2")
  }
}