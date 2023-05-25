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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.parsanatech.crazycoder.databinding.ActivityEmailVarificationSignUpBinding
import com.parsanatech.crazycoder.databinding.ActivitySignUpBinding
import com.parsanatech.crazycoder.databinding.ActivityWrongEmailsignupBinding
import kotlin.Exception


class SignUp : AppCompatActivity() {

    private val MODE_PRIVATE:Int=0
    lateinit var binding: ActivitySignUpBinding
    lateinit var auth:FirebaseAuth
    lateinit var firestoreDb:FirebaseFirestore
    var uniqueFirebaseAuthId:String?=null
    var googleSignInClient:GoogleSignInClient? =null
    var Muniqueusername:String?=null
    var finaldialog:Dialog?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()
        firestoreDb= FirebaseFirestore.getInstance()

        if(auth.currentUser!=null&&(auth.currentUser!!.isEmailVerified))
        {
            Log.d("CurrentUserIsNotNull So Logging","Yes")
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        val progressbar=binding.progressbar
        val Tick = binding.passwordTick


        binding.signin.setOnClickListener {
            Log.d("Clicked on signIn","Yes")
            startActivity(Intent(this,SignIn::class.java))
            finish()

        }

        try
        {
            binding.signup.setOnClickListener {

                Log.d("Clicked onSignUp","Yes")
                binding.progressbar.visibility=View.VISIBLE

                val EnteredEmail=binding.email.text.toString().trim()
                val EnteredPassword=binding.password.text.toString().trim()
                val EnteredConfirmPassword=binding.confirePassword.text.toString().trim()
                val userEnterdName=binding.uiqueusername.text.toString().trim()
                val passwordPattern = "(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}".toRegex()

                if(userEnterdName.isEmpty())
                {
                    Log.d("UserName is Empty","Yes")
                    binding.uiqueusername.setError("Please Enter your Name")
                    progressbar.visibility=View.INVISIBLE
                }
                if(EnteredEmail.isEmpty())
                {
                    Log.d("UserEmail is Empty","Yes")
                    progressbar.visibility=View.INVISIBLE
                    binding.email.setError("Please Enter Email")

                }
                if(!Patterns.EMAIL_ADDRESS.matcher(EnteredEmail).matches())
                {
                    Log.d("Invalid Email Patterns","$EnteredEmail")
                    binding.progressbar.visibility=View.INVISIBLE
                    binding.email.setError("Invalid Email")
                }
                if(EnteredPassword.isEmpty())
                {
                    Log.d("Entered Password is Empty","$EnteredPassword")
                    progressbar.visibility=View.INVISIBLE
                    binding.password.setError("Please Enter Password")

                }
                if(EnteredPassword.isNotEmpty()&&EnteredPassword.length<12)
                {
                    Log.d("Password Length Less than 12 char","Yes")
                    progressbar.visibility=View.INVISIBLE
                    binding.password.setError("Password should be at least 12 characters")

                }
                if (!passwordPattern.matches(EnteredPassword)) {
                    Log.d("Password does not meet the requirements", "$EnteredPassword")
                    binding.progressbar.visibility = View.INVISIBLE
                    binding.password.setError("Password should contain at least one uppercase letter, one lowercase letter, one symbol, and no spaces")
                }
                if(EnteredConfirmPassword.isEmpty())
                {
                    Log.d("Confirm Password is Empty","Yes")
                    progressbar.visibility=View.INVISIBLE
                    binding.confirePassword.setError("please Re-enter password")

                }
                if(EnteredPassword!=EnteredConfirmPassword)
                {
                    Log.d("Main password and confirm password is not same","$EnteredConfirmPassword != $EnteredPassword")
                    progressbar.visibility=View.INVISIBLE
                    binding.password.setError("Password is not matching")
                    binding.confirePassword.setError("Password is not matching")

                }
                if(EnteredPassword.isNotEmpty()&&EnteredPassword.length>=12 && passwordPattern.matches(EnteredPassword) && EnteredPassword==EnteredConfirmPassword)
                {
                    Tick.visibility = View.VISIBLE
                }
                else
                {
                    if(validate(userEnterdName))
                    {
                        Log.d("Every Thins are validated","Yes")
                        try {

                            firestoreDb.collection("users").document("userNameToId").get().addOnSuccessListener {

                                try {
                                    if (it[userEnterdName]==null) {

                                        Log.d("User Entered Unique UserName","$userEnterdName")


                                        auth.createUserWithEmailAndPassword(EnteredEmail,EnteredPassword).addOnCompleteListener { task ->

                                            Log.d("userAuthenticated", "yes")

                                            if (task.isSuccessful) {
                                                val currUser = auth.currentUser

                                                Log.d("TaskIsSuccessful", "Yes")

                                                currUser!!.sendEmailVerification()
                                                    .addOnSuccessListener {
                                                        try {

                                                            Log.d("Emailissent", "yes")

                                                            binding.progressbar.visibility = View.INVISIBLE

                                                            uniqueFirebaseAuthId =
                                                                task.result!!.user!!.uid.toString()

                                                            val sentEmailbinding =
                                                                ActivityEmailVarificationSignUpBinding.inflate(
                                                                    layoutInflater
                                                                )

                                                            finaldialog = Dialog(this@SignUp)
                                                            finaldialog!!.setContentView(sentEmailbinding.root)
                                                            finaldialog!!.setCanceledOnTouchOutside(true)
                                                            finaldialog!!.setCancelable(true)
                                                            finaldialog!!.window?.setBackgroundDrawable(
                                                                ColorDrawable(Color.TRANSPARENT)
                                                            )

                                                            sentEmailbinding.closebtn.setOnClickListener {

                                                                finaldialog!!.dismiss()

                                                                Log.d("Email sent Dialog close button pressed", "Yes")
                                                            }
                                                            finaldialog!!.show()

                                                            onResume()

                                                            getWindow().setFlags(
                                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                                            );
                                                        }
                                                        catch (e:Exception)
                                                        {
                                                            Log.e("Exception while showing Dialog",e.toString())
                                                        }

                                                    }.addOnFailureListener {

                                                        Log.d("Email varification sent Error", it.toString())

                                                    }
                                            }
                                            task.addOnFailureListener {
                                                Log.d("TaskFail", it.toString())
                                            }


                                        }.addOnFailureListener {

                                            try {
                                                throw it
                                            }
                                            catch (e:FirebaseAuthUserCollisionException)
                                            {
                                                Log.d("AlreadyEmailExist","${it.message}")
                                                val intent=Intent(this@SignUp,SignIn::class.java)
                                                intent.putExtra("Email",EnteredEmail)
                                                intent.putExtra("password",EnteredPassword)
                                                startActivity(intent)
                                                finish()
                                            }
                                            catch (e:Exception)
                                            {
                                                Log.d("Error in New user Authentication",e.toString())
                                                progressbar.visibility=View.INVISIBLE

                                                val dialogbinding=ActivityWrongEmailsignupBinding.inflate(layoutInflater)

                                                dialogbinding.message.setText(it.message.toString())

                                                val dialog=Dialog(this@SignUp)
                                                dialog.setContentView(dialogbinding.root)
                                                dialog.setCanceledOnTouchOutside(true)
                                                dialog.setCancelable(true)
                                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                                                dialogbinding.closebtn.setOnClickListener {
                                                    dialog.dismiss()
                                                }
                                                dialog.show()
                                            }

                                        }



                                    }
                                    else
                                    {
                                        if(uniqueFirebaseAuthId==null)
                                            Toast.makeText(this@SignUp,"Name ${userEnterdName} is already in use",Toast.LENGTH_LONG).show()
                                        progressbar.visibility=View.INVISIBLE
                                    }
                                }
                                catch (e:Exception)
                                {
                                    Log.e("Error while checking user in database",e.toString())
                                }


                            }.addOnFailureListener{
                                Log.d("Exception in First Read",it.message.toString())
                                Toast.makeText(this@SignUp,"Some Error occured while connecting server",Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e:Exception)
                        {
                            Log.e("Exception while storing user",e.toString())
                        }
                    }
                    else{
                        progressbar.visibility=View.INVISIBLE
                        binding.uiqueusername.setError("A-Z a-z 0-9 _ are allowed")
                    }

                }


            }
        }
        catch (e:Exception)
        {
            Log.e("Exception while signUp",e.toString())
        }



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_inString))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signinwithgoogle.setOnClickListener {

            progressbar.visibility=View.VISIBLE

            val username=binding.uiqueusername.text.toString().trim()

            if(username.isEmpty())
            {
                progressbar.visibility=View.INVISIBLE
                binding.uiqueusername.setError("userName is required")
            }
            else if(!validate(username))
            {
                progressbar.visibility=View.INVISIBLE
                binding.uiqueusername.setError("A-Z a-z 0-9 _ are allowed")
            }
            else{

                firestoreDb.collection("users").document("userNameToId").get().addOnSuccessListener {
                    if(it[username]==null)
                    {
                        Muniqueusername=username
                        signIn()
                    }
                    else
                    {
                        progressbar.visibility=View.INVISIBLE

                        if(uniqueFirebaseAuthId==null)
                            binding.uiqueusername.setError("Name ${username} is already in use")
                    }

                }.addOnFailureListener {
                    Log.e("Error while Reading stored name in signWithGoogle",it.message.toString())
                    Toast.makeText(this@SignUp,"Some Error occured while connecting server",Toast.LENGTH_LONG).show()
                }

            }


        }


    }

    val RC_SIGN_IN=10
    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

                Log.d("SignInWithGoogleApi Exception",e.message.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    uniqueFirebaseAuthId=user!!.uid

                    val finalUserINfirestore= hashMapOf(
                        "email" to user.email.toString().trim(),
                        "username" to Muniqueusername.toString(),
                        "status" to false,
                        "chatfriends" to ArrayList<String>()
                    )


                    firestoreDb.collection("users").document(auth.uid.toString()).set(finalUserINfirestore).addOnSuccessListener {

                        firestoreDb.collection("users").document("userNameToId").update(Muniqueusername.toString(),user.uid).addOnSuccessListener {


                            try {
                                Log.d("Storing sharedprefernce","yes")
                                val sharedPref = this.getSharedPreferences(
                                    "com.example.CrazyCoder", this.MODE_PRIVATE)?: return@addOnSuccessListener
                                with(sharedPref.edit())
                                {
                                    putString("userName",Muniqueusername.toString())
                                    commit()
                                }
                            }
                            catch (e:Exception)
                            {
                                Log.e("Exception while storing Sharedpreference",e.toString())
                            }


                            val intent=Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }





                } else {
                    Log.w("SignInFail", "signInWithCredential:failure", task.exception)
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

    override fun onResume() {
        super.onResume()

        Log.d("onResume called","Yes")
        CheckUserIsVerifiedAndGotoLoginScreen()

    }

    private fun CheckUserIsVerifiedAndGotoLoginScreen()
    {
        Log.d("InCheckUserIsVerifiedAndGotoLoginScreen","Yes")
        if(uniqueFirebaseAuthId!=null)
        {
            binding.progressbar.visibility=View.VISIBLE
            val curruser=auth.currentUser



          try {
              val handler = Handler(Looper.getMainLooper())

              var i=0
              val runnableCode: Runnable = object : Runnable {
                  override fun run() {


                      if(curruser!!.isEmailVerified)
                      {
                          Log.d("User verified Now saving user","yes")
                          saveUser(curruser)
                      }
                      else
                      {
                          i++
                          if(i==10)
                          {
                              curruser!!.reload()
                              Log.d("User Reloaded","Yes")
                              i=0
                          }
                          Log.d("Waiting for verification","Yes")
                          handler.postDelayed(this, 100)
                      }
                  }
              }

              handler.post(runnableCode)
          }
          catch (e:Exception)
          {
              Log.e("Error in handler Code SignUp Activity",e.toString())
          }


        }
    }



    fun saveUser(currUser:FirebaseUser)
    {

        try {
            val userEnterdName=binding.uiqueusername.text.toString().trim()

            if(currUser!!.isEmailVerified)
            {
                val uniqueAuthId=uniqueFirebaseAuthId.toString()

                val verifieduserInfirestore= hashMapOf(

                    "username" to userEnterdName,
                    "email" to binding.email.text.toString().trim(),
                    "pass" to binding.password.text.toString().trim(),
                    "status" to false,
                    "chatfriends" to ArrayList<String>()

                )

                firestoreDb.collection("users").document(uniqueAuthId).set(verifieduserInfirestore).addOnCompleteListener {

                    firestoreDb.collection("users").document("userNameToId").update(userEnterdName , uniqueAuthId).addOnSuccessListener {

                        try
                        {
                            if(finaldialog!=null) finaldialog!!.dismiss()
                        }
                        catch(e:Exception)
                        {
                            Log.e("Exception in dialog dismissing",e.toString())
                        }

                        val intent=Intent(this@SignUp,SignIn::class.java)
                        intent.putExtra("Email",binding.email.text.toString().trim())
                        intent.putExtra("password",binding.password.text.toString().trim())
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Log.e("Error in second request inside saveuser fun",it.message.toString())
                    }

                }.addOnFailureListener {
                    Log.e("ErrorInFirst Request in saveuser fun",it.message.toString())
                }

            }
            else
            {
                Log.d("User is not verified", "${currUser.toString() } -> ${currUser.isEmailVerified}")
                Toast.makeText(this@SignUp,"Please verify Your Account",Toast.LENGTH_LONG).show()
            }
        }
        catch (e:Exception)
        {
            Log.e("Exception in saveUser function",e.toString())
        }


    }

}