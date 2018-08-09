package com.cliplay.license

import android.content.Context
import com.cliplay.util.GradientFileParser
import com.google.gson.Gson
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


/**
 * Created by Manohar Peswani on 24/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */


data class License(val title: String, val url: String, val type: Int)

fun showLicense(context: Context) {
    val result = Single.fromCallable {
        val inputStream = context.assets.open("license.json")
        val jsonArray = JSONArray(GradientFileParser.convertStreamToString(inputStream))
        return@fromCallable Gson().fromJson(jsonArray.toString(), Array<com.cliplay.license.License>::class.java).toList()
    }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<com.cliplay.license.License>>() {
                override fun onSuccess(list: List<com.cliplay.license.License>) {
                    val dialog = LicenserDialog(context)
                            .setTitle("Licenses")
                            .setCustomNoticeTitle("Notices for files:")
                    dialog.setLibrary(Library("Android Support Libraries",
                            "https://developer.android.com/topic/libraries/support-library/index.html",
                            License.APACHE))
                    for (l in list) {
                        dialog.setLibrary(Library(l.title, l.url, l.type))
                    }
                    dialog.setPositiveButton(android.R.string.ok) { _, i ->
                    }.show()
                    dispose()
                }

                override fun onError(e: Throwable) {
                }
            })

}