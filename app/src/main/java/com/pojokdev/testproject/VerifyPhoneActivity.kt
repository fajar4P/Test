package com.pojokdev.testproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.pojokdev.testproject.data.ResponseLogin
import com.pojokdev.testproject.network.ApiService
import kotlinx.android.synthetic.main.activity_verify_phone.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class VerifyPhoneActivity : AppCompatActivity() {


    private var verificationId : String? = null


    override fun onCreate( savedInstanceState: Bundle?) {
        super.onCreate( savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        supportActionBar!!.title = "Verifikasi HP"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.GONE


        button_send_verification.setOnClickListener {


            val phone = edit_text_phone.text.toString().trim()

            if (phone.isEmpty()) {
                edit_text_phone.error = "Enter a valid phone"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }
            doLogin(phone)
            val phoneNumber = '+' + ccp.selectedCountryCode + phone

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    phoneAuthCallbacks
                )
        }


        button_verify.setOnClickListener {

            val code = edit_text_code.text.toString().trim()

            if(code.isEmpty()){
                edit_text_code.error = "Code required"
                edit_text_code.requestFocus()
                return@setOnClickListener
            }

            verificationId?.let{
                val credential = PhoneAuthProvider.getCredential(it, code)
                addPhoneNumber(credential)
            }
        }

    }

   //////////////////////////

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, token)
            this@VerifyPhoneActivity.verificationId = verificationId
        }


        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {

           phoneAuthCredential?.let {
                //addPhoneNumber(phoneAuthCredential)
            //    toast("verifikasi complate")
                    Log.d("onComplate", "onVerificationCompleted:$phoneAuthCredential")
            }


        }

        override fun onVerificationFailed(exception: FirebaseException?) {

            toast(exception?.message!!)
            //context?.toast(exception?.message!!)
        }


    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            //.currentUser?.updatePhoneNumber(phoneAuthCredential)
            .signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    toast("Logged in Successfully :)")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    toast(task.exception?.message!!)
                }
            }
    }

    /////////////////////////

    fun doLogin(nohp: String){
        onLoading(true)
        ApiService.endpoint.cekNohp(nohp)
            .enqueue(object  : Callback<ResponseLogin>{
                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    onLoading(false)
                }

                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    //onLoading(false)

                        val responseLogin: ResponseLogin? = response.body()
                        if(responseLogin!!.values == true){
                            toast("Nomor benar")
                            layoutPhone.visibility = View.GONE
                            layoutVerification.visibility = View.VISIBLE

                            onLoading( false)
                        } else{
                            toast("nomor salah")
                        }

                }

            })

    }

   fun onLoading(loading: Boolean){

        when(loading) {

            true -> {
                progressbar.visibility = View.VISIBLE
                button_verify.visibility = View.GONE
            }
            false -> {
            progressbar.visibility = View.GONE
            button_verify.visibility = View.VISIBLE
            }
        }
    }

    private fun toast (msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}
