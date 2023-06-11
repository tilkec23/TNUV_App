package si.uni_lj.fe.tnuv.umami_burger

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal class PrenosPodatkov(
    private val urlNaslov: String, // klicatelja potrebujemo zaradi konteksta
    private val callerActivity: Activity
) {
    fun prenesiPodatke(): String {
        val connMgr =
            callerActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo?
        networkInfo = try {
            connMgr.activeNetworkInfo
        } catch (e: Exception) {
            //je v manifestu dovoljenje za uporabo omrezja?
            return callerActivity.resources.getString(R.string.napaka_omrezje)
        }
        return if (networkInfo != null && networkInfo.isConnected) {
            try {
                connect()
            } catch (e: IOException) {
                callerActivity.resources.getString(R.string.napaka_povezava)
            }
        } else {
            callerActivity.resources.getString(R.string.napaka_omrezje)
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the content as a InputStream, which it returns as a string.
    @Throws(IOException::class)
    private fun connect(): String {
        val url = URL(urlNaslov)
        val conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 5000
        conn.connectTimeout = 10000
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.setRequestProperty("Accept", "application/json")
        conn.connect() // Starts the query

        // blokira, dokler ne dobi odgovora
        val response = conn.responseCode

        // Convert the InputStream into a string
        return convertStreamToString(conn.inputStream)
    }

    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}