package com.d121211069.submissionawalandroidintermediate.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.d121211069.submissionawalandroidintermediate.data.remote.response.LoginResult
import com.d121211069.submissionawalandroidintermediate.databinding.ActivityLoginBinding
import com.d121211069.submissionawalandroidintermediate.factory.ViewModelFactory
import com.d121211069.submissionawalandroidintermediate.util.Result
import com.d121211069.submissionawalandroidintermediate.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private val gmailPattern = "[a-zA-Z0-9._-]+@gmail\\.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMyButtonEnable()

        binding.emailEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            setMyButtonEnable()
        })

        binding.passwordEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            setMyButtonEnable()
        })

        setupAction()
        playAnimation()
    }

    private fun setMyButtonEnable() {
        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString()

        binding.loginButton.isEnabled =
            password.length >= 8 && email.matches(gmailPattern.toRegex())
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(350)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(350)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(350)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(350)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(350)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(350)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(350)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            start()
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            loginViewModel.loginUser(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            val responseResult = result.data.loginResult
                            loginViewModel.saveSession(
                                LoginResult(
                                    responseResult.name, responseResult.userId, responseResult.token
                                )
                            )
                            showLoading(false)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                            showLoading(false)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}