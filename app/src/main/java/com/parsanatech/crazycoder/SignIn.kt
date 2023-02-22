package com.parsanatech.crazycoder

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.parsanatech.crazycoder.databinding.*
import com.parsanatech.crazycoder.models.UseratFirebase
import io.grpc.internal.LogExceptionRunnable
import java.lang.Exception


class SignIn : AppCompatActivity() {

    private val MODE_PRIVATE:Int=0
    lateinit var binding:ActivitySignInBinding
    lateinit var auth:FirebaseAuth
    lateinit var firestoreDb:FirebaseFirestore
    var name=""
    var checkforonreume=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth= FirebaseAuth.getInstance()

        firestoreDb= FirebaseFirestore.getInstance()

        try {
            val sharedPref = this.getSharedPreferences(
                "com.example.CrazyCoder", this.MODE_PRIVATE)?: return
            with(sharedPref.edit())
            {
                putString("CODEFORCES","")
                putString("LEETCODE","")
                putString("SPOJ","")
                apply()
            }

        }
        catch (e:Exception)
        {
            Log.e("Exception in SharedPreferences",e.toString())
        }



        binding.signup.setOnClickListener {
            Log.d("Clicked on SignUp button","yes")
            startActivity(Intent(this,SignUp::class.java))
            finish()

        }


        val email = intent.getStringExtra("Email")
        val password = intent.getStringExtra("password")

        binding.useremail.setText(email)
        binding.userpassword.setText(password)

        val progressbar=binding.progressbar

        binding.btnSignIn.setOnClickListener {

            Log.d("Sign in Button Clicked","Yes")
            progressbar.visibility=View.VISIBLE

            val EnteredEmail=binding.useremail.text.toString().trim()
            val Enteredpassword=binding.userpassword.text.toString().trim()

            if(EnteredEmail.isEmpty())
            {
                Log.d("Email is empty","$EnteredEmail")
                progressbar.visibility=View.INVISIBLE
                binding.useremail.setError("Plaese Enter Your Email")
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(EnteredEmail).matches())
            {
                Log.d("Invalid Email","$EnteredEmail")
                binding.progressbar.visibility=View.INVISIBLE
                binding.useremail.setError("Invalid Email")
            }
            if(Enteredpassword.isEmpty())
            {
                Log.d("Password is empty",Enteredpassword)
                progressbar.visibility=View.INVISIBLE
                binding.userpassword.setError("Please Enter Password")
            }
            if(Enteredpassword.length<6)
            {
                Log.d("Password less than 6 cahr","Yes")
                progressbar.visibility=View.INVISIBLE
                binding.userpassword.setError("Password should be at least 6 characters")
            }
            else{

                Log.d("Eveery thing are Verified","Yes")
                auth.signInWithEmailAndPassword(EnteredEmail,Enteredpassword).addOnCompleteListener {task->

                    Log.d("Signing in","Yes")
                    if(task.isSuccessful)
                    {
                        Log.d("Task is successful","Yes")
                        if(auth.currentUser!!.isEmailVerified)
                        {
                            Log.d("User Email Verified","Yes")
                            Log.d("unverifiedForgotPass","Yes")

                            if(name.isEmpty())
                            {
                                Log.d("UserName is Empty so Reading from firestore to save in sharedpreference","Yes")
                                firestoreDb.collection("users").document(auth.uid.toString()).get().addOnSuccessListener {

                                    try {
                                        Log.d("Storing sharedprefernce","yes")
                                        val sharedPref = this.getSharedPreferences(
                                            "com.example.CrazyCoder", this.MODE_PRIVATE)?: return@addOnSuccessListener
                                        with(sharedPref.edit())
                                        {
                                            putString("userName",it["username"].toString())
                                            commit()
                                        }
                                    }
                                    catch (e:Exception)
                                    {
                                        Log.e("Exception while storing Sharedpreference",e.toString())
                                    }

                                }.addOnFailureListener {
                                    Log.e("Error in reading ",it.toString())
                                }
                            }
                            else{
                                Log.d("userName is not empty",name)
                                try {
                                    Log.d("Storing sharedpreference","Yes")
                                    val sharedPref = this.getSharedPreferences(
                                        "com.example.CrazyCoder", this.MODE_PRIVATE)?: return@addOnCompleteListener
                                    with(sharedPref.edit())
                                    {
                                        putString("userName",name)
                                        commit()
                                    }
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Exception while storing sharedpreference","Yes")
                                }

                            }
                            Log.d("Going to main Activity","Yes")
                            val intent=Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Log.d("Unverified User calling unverifieduser() fun","Yes")
                            Unverifieduser()
                        }


                    }


                }.addOnFailureListener {

                    Log.e("Error while signin",it.toString())
                    progressbar.visibility=View.INVISIBLE

                    try {

                        val dialogbinding=ActivityWrongEmailsignupBinding.inflate(layoutInflater)

                        dialogbinding.message.setText(it.message.toString())

                        val dialog=Dialog(this)
                        dialog.setContentView(dialogbinding.root)
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.setCancelable(true)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        dialogbinding.closebtn.setOnClickListener {
                            Log.d("Dialog is closed","Yes")
                            dialog.dismiss()
                        }

                        dialog.show()

                    }
                    catch (e:Exception)
                    {
                        Log.e("Exception while showing Error message dialog",e.toString())
                    }
                }

            }


        }

        binding.Forgotpass.setOnClickListener {

            Log.d("Clicked on forgotPassword","Yes")

            try {

                val passwordresetactivity=ActivityPasswordResetBinding.inflate(layoutInflater)

                val dialog = Dialog(this)
                dialog.setContentView(passwordresetactivity.root)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                passwordresetactivity.userinput.setText(binding.useremail.text.toString().trim())

                dialog.show()

                passwordresetactivity.submitbtn.setOnClickListener {

                    Log.d("Clicked on submit Button of Dialog","Yes")
                    passwordresetactivity.progressbar.visibility

                    passwordresetactivity.progressbar.visibility=View.VISIBLE

                    val inneremail=passwordresetactivity.userinput.text.toString().trim()

                    if(inneremail.isEmpty())
                    {
                        passwordresetactivity.progressbar.visibility=View.INVISIBLE
                        passwordresetactivity.userinput.setError("please Enter Email")
                    }
                    if(!Patterns.EMAIL_ADDRESS.matcher(inneremail).matches())
                    {
                        passwordresetactivity.progressbar.visibility=View.INVISIBLE
                        passwordresetactivity.userinput.setError("Invalid Email")
                    }
                    else
                    {
//                    auth.cp;

                        auth.sendPasswordResetEmail(inneremail).addOnSuccessListener {

                            dialog.dismiss()

                            try {
                                val innerDialogBinding=ActivityPasswordResetLinkSentBinding.inflate(layoutInflater)

                                val innerDialog=Dialog(this)
                                innerDialog.setContentView(innerDialogBinding.root)
                                innerDialog.setCanceledOnTouchOutside(true)
                                innerDialog.setCancelable(true)
                                innerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                innerDialogBinding.closebtn.setOnClickListener {

                                    innerDialog.dismiss()

                                }
                                passwordresetactivity.progressbar.visibility=View.INVISIBLE
                                innerDialog.show()
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception in innerDialog",e.toString())
                            }


                        }.addOnFailureListener {

                            dialog.dismiss()

                            try {
                                val failedialogbinding=ActivityWrongEmailsignupBinding.inflate(layoutInflater)

                                failedialogbinding.message.text=it.message.toString()
                                failedialogbinding.detailmessage.visibility=View.GONE

                                val innerfailDialog=Dialog(this)
                                innerfailDialog.setContentView(failedialogbinding.root)
                                innerfailDialog.setCanceledOnTouchOutside(true)
                                innerfailDialog.setCancelable(true)
                                innerfailDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                //on resume

                                failedialogbinding.closebtn.setOnClickListener {
                                    innerfailDialog.dismiss()
                                }

                                innerfailDialog.show()
                            }
                            catch (e:Exception)
                            {
                                Log.e("Error in Failed Dialog showing",e.toString())
                            }

                        }

                    }



                }
            }
            catch (e:Exception)
            {
                Log.e("Error in showing password Reset Dialog",e.toString())
            }


        }



    }

    fun Unverifieduser()
    {

        val progressbar=binding.progressbar

        progressbar.visibility=View.INVISIBLE


        try {
            val dialogbinding=ActivityWrongEmailsignupBinding.inflate(layoutInflater)

            dialogbinding.closebtn.setText("verify now")
            dialogbinding.message.setText("Please verify your Email")
            dialogbinding.detailmessage.setText("Your email is registered but not verified please verify")


            val dialog=Dialog(this)
            dialog.setContentView(dialogbinding.root)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogbinding.closebtn.setOnClickListener {      //close button in wrong email signup binding is changed as verify now but is is still same

                dialog.dismiss()
                binding.progressbar.visibility=View.VISIBLE

                auth.currentUser!!.sendEmailVerification().addOnSuccessListener {

                    binding.progressbar.visibility = View.INVISIBLE
                    checkforonreume=true

                    try {
                        val sentEmailbinding =
                            ActivityEmailVarificationSignUpBinding.inflate(
                                layoutInflater
                            )

                        val innerdialog = Dialog(this)
                        innerdialog.setContentView(sentEmailbinding.root)
                        innerdialog.setCanceledOnTouchOutside(true)
                        innerdialog.setCancelable(true)
                        innerdialog.window?.setBackgroundDrawable(
                            ColorDrawable(Color.TRANSPARENT)
                        )

                        sentEmailbinding.closebtn.setOnClickListener {

                            innerdialog.dismiss()

                            Log.d("sentEmailverification Dialog Closed", "Yes")
                        }
                        innerdialog.show()

                    }
                    catch (e:Exception)
                    {
                        Log.e("Error while showing sentEmailverification Dialog",e.toString())
                    }

                }.addOnFailureListener {
                    Log.e("Fail to send verification Email in Signin Activity",it.toString())
                }

            }

            dialog.show()
        }
        catch (e:Exception)
        {
            Log.e("Exception while showing Emailverification Dialog",e.toString())
        }

    }


    override fun onResume() {
        super.onResume()


        Log.d("onResume","SignIn Activity")
        val curruser=auth.currentUser


        if(curruser!=null&&(!curruser.isEmailVerified)&&checkforonreume){

            curruser.reload()

            var i=0

            try {
                val handler = Handler(Looper.getMainLooper())

                val runnableCode: Runnable = object : Runnable {
                    override fun run() {

                        if(curruser.isEmailVerified)
                        {
                            Log.d("User is verified Now saving user calling saveUser fun","Yes")
                            saveUser(curruser)
                        }
                        else
                        {
                            i++
                            if(i==10)
                            {
                                Log.w("Reloading User","yes")
                                curruser.reload()
                                i=0
                            }
                            Log.d("Waithing for user verification","Yes")
                            handler.postDelayed(this, 100)

                        }
                    }
                }

                handler.post(runnableCode)

            }
            catch (e:Exception)
            {
                Log.e("Error in handler SignIn Activity",e.toString())
            }

        }


    }

    fun saveUser(curruser:FirebaseUser)
    {
        if(curruser!=null&&curruser.isEmailVerified)
        {

            try {
                val dialogbinding=ActivityPasswordResetBinding.inflate(layoutInflater)

                dialogbinding.mainmessage.setText("Enter Username")
                dialogbinding.userinput.setHint("CrazyCoder Username")

                val dialog=Dialog(this)
                dialog.setContentView(dialogbinding.root)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialogbinding.submitbtn.setOnClickListener {

                    Log.d("Submit button is pressed in userName taking Dialog","Yes")
                    dialogbinding.progressbar.visibility=View.VISIBLE

                    val username=dialogbinding.userinput.text.toString().trim()

                    if(username.isEmpty())
                    {
                        Log.d("UserName is Empty","Yes")
                        dialogbinding.userinput.setError("Please Enter Name")
                        dialogbinding.progressbar.visibility=View.INVISIBLE
                    }
                    if(!validate(username))
                    {
                        Log.d("Invalid Username",username)
                        dialogbinding.userinput.setError("A-Z a-z 0-9 _ are allowed")
                        dialogbinding.progressbar.visibility=View.INVISIBLE

                    }
                    else{

                        var avoidRpeatation=true

                        firestoreDb.collection("users").document("userNameToId").get().addOnSuccessListener {

                            try {
                                if(it[username]==null)
                                {
                                    firestoreDb.collection("users").document(curruser.uid).set(
                                        hashMapOf("username" to username,
                                            "email" to curruser.email.toString(),
                                            "status" to false,
                                            "chatfriends" to ArrayList<String>()
                                        )).addOnSuccessListener {
                                        name=username
                                        firestoreDb.collection("users").document("userNameToId").update(username , curruser.uid).addOnSuccessListener {

                                            avoidRpeatation=false
                                            dialog.dismiss()

                                        }.addOnFailureListener {
                                            Log.e("Error in storing userName:id pair",it.toString())
                                            Toast.makeText(this@SignIn,it.message.toString(),Toast.LENGTH_LONG).show()
                                        }
                                    }.addOnFailureListener {
                                        Log.e("Error while storing user on Firestore",it.toString())
                                        Toast.makeText(this@SignIn,it.message.toString(),Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    dialogbinding.progressbar.visibility=View.INVISIBLE
                                    if(avoidRpeatation)
                                        Toast.makeText(this@SignIn,"Name ${username} is already in use",Toast.LENGTH_LONG).show()

                                }
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception in Reading save user SignIn Activity",e.toString())
                            }

                        }.addOnFailureListener {
                            Log.e("Error in Reading username:id pair in saveuser signIn Activity",it.toString())
                            dialogbinding.progressbar.visibility=View.INVISIBLE
                            Toast.makeText(this@SignIn,it.message.toString(),Toast.LENGTH_LONG).show()
                        }


                    }


                }


                dialog.show()
            }
            catch (e:Exception)
            {
                Log.e("Error in userName taking Dialog",e.toString())
            }

        }


    }


    fun validate(userEnterdName:String):Boolean{

        for(i in 0..userEnterdName.length-1)
        {
            if(!((userEnterdName[i]>='a'&&userEnterdName[i]<='z')||(userEnterdName[i]>='A'&&userEnterdName[i]<='Z')||(userEnterdName[i]>='0'&&userEnterdName[i]<='9')||userEnterdName[i]=='_'))
            {
                return false
            }
        }
        return true
    }




}
