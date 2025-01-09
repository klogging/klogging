/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.sending

import java.net.HttpURLConnection
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * A trust modifier for accepting any TLS certificates.
 */
internal object Certificates {

    private val TRUSTING_HOSTNAME_VERIFIER = TrustingHostnameVerifier()
    private val factory: SSLSocketFactory by lazy { prepFactory() }

    fun relaxHostChecking(conn: HttpURLConnection) {
        if (conn is HttpsURLConnection) {
            conn.sslSocketFactory = factory
            conn.hostnameVerifier = TRUSTING_HOSTNAME_VERIFIER
        }
    }

    @Synchronized
    fun prepFactory(): SSLSocketFactory {
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(null, arrayOf<TrustManager>(AlwaysTrustManager()), null)
        return ctx.socketFactory
    }

    private class TrustingHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

    /** A trust-everything trust manager. */
    private class AlwaysTrustManager : X509TrustManager {
        override fun checkClientTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
        }

        override fun checkServerTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}
