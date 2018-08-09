package com.cliplay.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import com.cliplay.model.WebGradient
import com.fxn.ariana.Ariana
import com.fxn.ariana.GradientAngle
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * Created by Manohar Peswani on 02/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
object GradientFileParser {
    @Throws(IOException::class, JSONException::class)
    private fun getWebGradients(context: Context): List<WebGradient> {
        val inputStream = context.assets.open("grad.txt")
        val jsonArray = JSONObject(convertStreamToString(inputStream))
        val webGradients = ArrayList<WebGradient>()
        val iterable = jsonArray.keys()
        while (iterable.hasNext()) {
            val webGradient = WebGradient()
            val name = iterable.next()
            webGradient.name = name
            val backgroundImage = JSONObject(jsonArray.getString(name)).getString("backgroundImage")
            val colors = backgroundImage.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            webGradient.degree = colors[0]
            val colorsList = ArrayList<WebGradient.Colors>()
            for (i in 1 until colors.size) {
                val color = colors[i].trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val colors1 = WebGradient.Colors()
                colors1.color = color[0]
                colors1.percent = color[1]
                colorsList.add(colors1)
            }
            webGradient.colors = colorsList
            if (colors[0].startsWith("to top")) {
                webGradient.gradientType = WebGradient.GradientType.TO_TOP
            } else if (colors[0].startsWith("to right")) {
                webGradient.gradientType = WebGradient.GradientType.TO_RIGHT
            } else if (colors[0].contains("deg")) {
                webGradient.gradientType = WebGradient.GradientType.DEGREE
            }
            Log.v("array", " $name : $backgroundImage")
            webGradients.add(webGradient)
        }
        return webGradients
    }

    internal fun convertStreamToString(`is`: java.io.InputStream): String {
        val s = java.util.Scanner(`is`).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    @Throws(IOException::class, JSONException::class)
    fun getGradientDrawables(context: Context): MutableList<Drawable> {
        val gradientDrawables = ArrayList<Drawable>()
        val webGradients = getWebGradients(context)
        for (webGradient in webGradients) {
            val colorsList = webGradient.colors
            val gradColors = IntArray(colorsList.size)
            for (i in colorsList.indices) {
                gradColors[i] = getColoHex(colorsList[i])
            }
            var angle = GradientAngle.LEFT_BOTTOM_TO_RIGHT_TOP
            val gradientType = webGradient.gradientType
            if (gradientType == WebGradient.GradientType.TO_RIGHT) {
                angle = GradientAngle.LEFT_TO_RIGHT
            } else if (gradientType == WebGradient.GradientType.DEGREE) {
                val degree = webGradient.degree
                if (degree == "45deg") {
                    angle = GradientAngle.LEFT_BOTTOM_TO_RIGHT_TOP
                } else if (degree == "120deg") {
                    angle = GradientAngle.RIGHT_TOP_TO_LEFT_BOTTOM
                } else if (degree == "180deg") {
                    angle = GradientAngle.RIGHT_TO_LEFT
                }
            }
            gradientDrawables.add(Ariana.drawable(gradColors, angle))
        }
        return gradientDrawables
    }

    private fun getColoHex(colors: WebGradient.Colors): Int {
        val color = colors.color
        //        String percent = colors.getPercent();
        //        Integer dec = Integer.parseInt(percent.replaceAll("%", "")) * 255 / 100;
        //        String hex = Integer.toHexString(dec);
        //        if (hex.length() == 1) {
        //            hex = hex + "0";
        //        }
        //        if (hex.length() > 2) {
        //            hex = hex.substring(0, 2);
        //        }
        //        String myColor = color.substring(0, 1) + hex + color.substring(1, color.length());
        //        Log.v("color",   "==>" + color);
        return Color.parseColor(color)
    }
}
