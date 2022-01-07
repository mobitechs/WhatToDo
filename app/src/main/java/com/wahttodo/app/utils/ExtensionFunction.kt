package com.wahttodo.app.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.AlertDialogBtnClickedCallBack
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.callbacks.SpinnerItemSelectedCallback
import com.wahttodo.app.model.AllMoviesList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.view.activity.AuthActivity
import com.wahttodo.app.view.activity.HomeActivity
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


var snackbar: Snackbar? = null
var progressBar: ProgressBar? = null


fun Context.checkLogin() {
    if (SharePreferenceManager.getInstance(this).getValueBoolean(Constants.ISLOGIN)) {
        openActivity(HomeActivity::class.java)
    } else {
        openActivity(AuthActivity::class.java)
    }

}

fun Context.showToastMsg(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showToastMsgLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}


fun showSnackBar(view: View, str: String) {
    snackbar = Snackbar
        .make(view, str, Snackbar.LENGTH_SHORT)
    snackbar!!.show()
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)

}

fun <T> Context.openClearActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    intent.putExtras(Bundle().apply(extras))
    if (!Bundle().apply(extras).isEmpty) {
        intent.putExtras(Bundle().apply(extras))
    }
    startActivity(intent)

}

fun Context.openCallLauncher(mobile: String) {
    val u: Uri = Uri.parse("tel:" + mobile)
    val i = Intent(Intent.ACTION_DIAL, u)
    try {
        startActivity(i)
    } catch (s: SecurityException) {
        showToastMsg(s.message.toString())
    }
}


fun AppCompatActivity.replaceFragmentWithData(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String,
    bundle: Bundle
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragment?.setArguments(bundle)
    fragmentTransaction.replace(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag);
    fragmentTransaction.commit()

}

fun AppCompatActivity.replaceFragment(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(containerViewId, fragment!!, flag)
    if (!supportFragmentManager.isStateSaved) {
        fragmentTransaction.commit()
    } else if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.addFragmentWithData(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String,
    bundle: Bundle
) {

    val fragmentManager: FragmentManager = supportFragmentManager

    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragment?.setArguments(bundle)
    fragmentTransaction.add(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag);
    fragmentTransaction.commit()

}

fun AppCompatActivity.addFragment(
    fragment: Fragment?,
    allowStateLoss: Boolean = false,
    @IdRes containerViewId: Int,
    flag: String
) {

    val fragmentManager: FragmentManager = supportFragmentManager
    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(containerViewId, fragment!!, flag)
    fragmentTransaction.addToBackStack(flag)
    if (!supportFragmentManager.isStateSaved) {
        fragmentTransaction.commit()
    } else if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    }
}


fun setStatusColor(window: Window, color: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(color)
    }
}

fun apiPostCall(url: String, jsonObject: JSONObject, apiResponse: ApiResponse, tag: String) {
    try {
        AndroidNetworking.post(url)
            .addJSONObjectBody(jsonObject)
            .setTag(tag)
            .setPriority(Priority.MEDIUM)
            .setMaxAgeCacheControl(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.get("Response") is JSONArray) {
                            val arr = response.getJSONArray("Response")
                            apiResponse.onSuccess(arr, tag)
                        } else if (response?.get("Response") is String) {
                            val msg = response.getString("Response")
                            //showToastMsg(msg)
                            apiResponse.onSuccess(response.getString("Response").toString(), tag)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                        apiResponse.onFailure(response?.getString("Response").toString())
                    }
                }

                override fun onError(error: ANError) {
                    error.errorDetail
                    apiResponse.onFailure(error.errorDetail)
                }
            })
    } catch (e: java.lang.Exception) {
        apiResponse.onFailure(e.message.toString())
    }
}

fun apiGetCall(url: String, apiResponse: ApiResponse, tag: String) {
    try {

        AndroidNetworking.get(url)
            .setTag(tag)
            .setPriority(Priority.MEDIUM)
            .setMaxAgeCacheControl(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.get("Response") is JSONArray) {
                            val arr = response.getJSONArray("Response")
                            apiResponse.onSuccess(arr, tag)
                        } else if (response?.get("Response") is String) {
                            val msg = response.getString("Response")
                            //showToastMsg(msg)
                            apiResponse.onSuccess(response.getString("Response").toString(), tag)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                        apiResponse.onFailure(response?.getString("Response").toString())
                    }
                }

                override fun onError(error: ANError) {
                    error.errorDetail
                    apiResponse.onFailure(error.errorDetail)
                }
            })

    } catch (e: java.lang.Exception) {
        apiResponse.onFailure(e.message.toString())
    }
}
fun apiGetCall2(url: String, apiResponse: ApiResponse, tag: String) {
    try {

        AndroidNetworking.get(url)
            .setTag(tag)
            .setPriority(Priority.MEDIUM)
            .setMaxAgeCacheControl(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        if (response.get("results") is JSONArray) {
                            val arr = response.getJSONArray("results")
                            apiResponse.onSuccess(arr, tag)
                        } else if (response?.get("results") is String) {
                            val msg = response.getString("results")
                            //showToastMsg(msg)
                            apiResponse.onSuccess(response.getString("results").toString(), tag)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        e.message
                        apiResponse.onFailure(response?.getString("results").toString())
                    }
                }

                override fun onError(error: ANError) {
                    error.errorDetail
                    apiResponse.onFailure(error.errorDetail)
                }
            })

    } catch (e: java.lang.Exception) {
        apiResponse.onFailure(e.message.toString())
    }
}

fun Context.showDatePickerDialog(v: View, txtDate: TextView) {

    var calendar = Calendar.getInstance()
    var year = calendar!!.get(Calendar.YEAR)
    var month = calendar!!.get(Calendar.MONTH)
    var day = calendar!!.get(Calendar.DAY_OF_MONTH)
    val mDatePicker = DatePickerDialog(
        v.context,
        DatePickerDialog.OnDateSetListener { datepicker, selectedyear, selectedmonth, selectedday ->
            // TODO Auto-generated method stub
            /*      Your code   to get date and time    */
            var month: Int = selectedmonth + 1
            var selectedDateForSubmission =
                selectedday.toString() + "-" + month + "-" + selectedyear.toString();
//                     toast(selectedday.toString() + "-" + (selectedmonth + 1).toString() + "-" + selectedyear.toString())

            txtDate.text = selectedDateForSubmission
        },
        year,
        month,
        day
    )
    mDatePicker.show()

}

fun Context.getDateFromPicker(v: View): String {

    var calendar = Calendar.getInstance()
    var year = calendar!!.get(Calendar.YEAR)
    var month = calendar!!.get(Calendar.MONTH)
    var day = calendar!!.get(Calendar.DAY_OF_MONTH)
    var selectedDateForSubmission = ""
    val mDatePicker = DatePickerDialog(
        v.context,
        DatePickerDialog.OnDateSetListener { datepicker, selectedyear, selectedmonth, selectedday ->
            // TODO Auto-generated method stub
            /*      Your code   to get date and time    */
            var month: Int = selectedmonth + 1
            selectedDateForSubmission =
                selectedday.toString() + "-" + month + "-" + selectedyear.toString();
//                     toast(selectedday.toString() + "-" + (selectedmonth + 1).toString() + "-" + selectedyear.toString())


        },
        year,
        month,
        day
    )
    mDatePicker.show()
    return selectedDateForSubmission
}

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun ImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}

fun AppCompatImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}

fun AppCompatImageView.setImage(image: Any) {
    val options: RequestOptions = RequestOptions().error(R.drawable.img_not_available)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);


//    .skipMemoryCache(true)
//        .diskCacheStrategy(DiskCacheStrategy.NONE)

}

fun CircleImageView.setImage(image: Any, default: Int) {
    val options: RequestOptions = RequestOptions().error(default)
    Glide.with(context).load(image)
        .apply(options)
        .into(this);

}

fun Context.ShareText(content: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun getRandomNumberString(): String {
    val rnd = Random()
    val number = rnd.nextInt(999999)
    return String.format("%06d", number)
}


fun hideKeyboard(view: View, activity: Context) {
    val `in` = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    `in`!!.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun parseDateToddMMyyyy(time: String?): String? {
    val inputPattern = "dd-MM-yyyy_HH:mm:ss"
    val outputPattern = "dd-MMM-yyyy hh:mm a"
    val inputFormat = SimpleDateFormat(inputPattern)
    val outputFormat = SimpleDateFormat(outputPattern)
    var date: Date? = null
    var str: String? = null
    try {
        date = inputFormat.parse(time)
        str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}


fun Context.getSpinnerSelectedValue(
    spinnerValueArray: Array<String>,
    spinner: Spinner,
    spinnerFor: String,
    event: SpinnerItemSelectedCallback
) {


    val adapter =
        ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerValueArray)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.setAdapter(adapter)

    spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(p0: AdapterView<*>?) {
//            event.onNothingSelected()
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            if (p2 > 0) {

                event.onItemSelected(p2, spinnerFor)
                showToastMsg(p2.toString() + " " + spinnerValueArray[p2])
            }
        }

    })
}


fun Context.showAlertDialog(
    title: String,
    description: String,
    positiveBtnText: String,
    negativeBtnText: String,
    alertDialogBtnClickedCallBack: AlertDialogBtnClickedCallBack
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(description)
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    builder.setPositiveButton(positiveBtnText) { dialog, which ->
        alertDialogBtnClickedCallBack.positiveBtnClicked()
    }

    builder.setNegativeButton(negativeBtnText) { dialog, which ->
        alertDialogBtnClickedCallBack.negativeBtnClicked()
        dialog.dismiss()
    }

    builder.show()
}


fun getTodaysDate(): String {
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
    var todaysDate = simpleDateFormat.format(Date())
    return todaysDate
}

fun getTodaysDateTime(): String {
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa")
    var todaysDate = simpleDateFormat.format(Date())
    return todaysDate
}


fun Context.setupCommonRecyclerViewsProperty(recyclerView: RecyclerView, type: String) {
    var mLayoutManager: LinearLayoutManager? = null
    if(type.equals(Constants.VERTICAL)){
        mLayoutManager = LinearLayoutManager(this)
    }
    else if(type.equals(Constants.HORIZONTAL)){
        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    recyclerView.layoutManager = mLayoutManager
    recyclerView.smoothScrollToPosition(0)
    recyclerView.isNestedScrollingEnabled = true
//    recyclerView.itemAnimator = SlideInUpAnimator()

}


fun Context.setupCommonGridRecyclerViewsProperty(recyclerView: RecyclerView, count: Int) {
    var gLayoutManager = GridLayoutManager(this, count)
    recyclerView.layoutManager = gLayoutManager
    recyclerView.hasFixedSize()
    recyclerView.smoothScrollToPosition(0)
    recyclerView.isNestedScrollingEnabled = false
    recyclerView.itemAnimator = DefaultItemAnimator()


}

fun Context.ShareRoomLink(
    activity: Activity,
    roomId: String,
    userId: String
) {

    ///manual Url Link Text
    val manualUrlLinkText = "https://wahttodo.page.link/?" +
            "link=http://www.mobitechs.in/WhatToDo/api/whattodo.php?referalUserId=$roomId"+"_a_"+userId +
//            "link=http://level.game/api/level.php?referalUserId=$roomId"+"_"+userId +
            "&apn=" + packageName +
            //"&ibn=com.wahttodo.app"+
            "&st=" + "Hey! Lets Decide Together." +
            "&sd=" + "Join this room so can decide together what to do." +
            "&si=" + "https://d5rp90v83afv2.cloudfront.net/logos/LevelCircle.png"

    val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLongLink(Uri.parse(dynamicLink.uri.toString()))
        .setLongLink(manualUrlLinkText.toUri()) // this is for manual url
        .buildShortDynamicLink()
        .addOnCompleteListener(
            activity
        ) { task ->
            if (task.isSuccessful) {
                // Short link created
                val shortLink = task.result.shortLink
                val flowchartLink = task.result.previewLink
                Log.e("main", "short Refer " + shortLink)
//                    showToastMsg(shortLink.toString())
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } else {
                Log.e("main", "Error " + task.exception)
                showToastMsg(task.exception.toString())
            }
        }
}

fun getAllMoviesList(listItems: ArrayList<AllMoviesList>): ArrayList<AllMoviesList> {
    listItems.clear()
    listItems.add(AllMoviesList("https://upload.wikimedia.org/wikipedia/en/thumb/c/cc/K.G.F_Chapter_1_poster.jpg/220px-K.G.F_Chapter_1_poster.jpg", "KGF", "5", "Best Movie of south", "0", "Hindi", "Action"))
    listItems.add(AllMoviesList("https://m.media-amazon.com/images/M/MV5BNDExMTBlZTYtZWMzYi00NmEwLWEzZGYtOTA1MDhmNTc0ODZkXkEyXkFqcGdeQXVyODE5NzE3OTE@._V1_.jpg", "Hera Pheri", "4", "Best comedy movie", "0", "Hindi", "Comedy"))
    listItems.add(AllMoviesList("https://m.media-amazon.com/images/M/MV5BNTEwMWJlMWUtNGI3ZC00NzhmLWI1M2ItNGE1NTBiMjk5NmYyXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg", "Kasoor", "2", "Old Hindi Movie", "0", "Hindi", "Romance"))
    listItems.add(AllMoviesList("https://upload.wikimedia.org/wikipedia/en/e/e1/Joker_%282019_film%29_poster.jpg", "Joker", "5", "Best English Movie", "0", "English", "Horror"))
    listItems.add(AllMoviesList("https://m.media-amazon.com/images/M/MV5BNTkyOGVjMGEtNmQzZi00NzFlLTlhOWQtODYyMDc2ZGJmYzFhXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg", "3 Idiots", "5", "Best youth movie", "0", "Hindi", "Comedy"))
    return listItems
}