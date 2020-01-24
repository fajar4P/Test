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
import kotlinx.android.synthetic.main.activity_verify_phone.*
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

            //
            val phone = edit_text_phone.text.toString().trim()

            if (phone.isEmpty()) {
                edit_text_phone.error = "Enter a valid phone"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }

            val phoneNumber = '+' + ccp.selectedCountryCode + phone

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    phoneAuthCallbacks
                )


            //
            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
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


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    //////////////////////////

    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {
            phoneAuthCredential?.let {
                addPhoneNumber(phoneAuthCredential)
            }
        }

        override fun onVerificationFailed(exception: FirebaseException?) {

            toast(exception?.message!!)
            //context?.toast(exception?.message!!)
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, token)
            this@VerifyPhoneActivity.verificationId = verificationId
        }
    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            .currentUser?.updatePhoneNumber(phoneAuthCredential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Logged in Successfully :)")
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    toast(task.exception?.message!!)
                }
            }
    }

    private fun toast (msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
    /////////////////////////
}
