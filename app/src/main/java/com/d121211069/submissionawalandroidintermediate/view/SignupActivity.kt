package com.d121211069.submissionawalandroidintermediate.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.d121211069.submissionawalandroidintermediate.databinding.ActivitySignupBinding
import com.d121211069.submissionawalandroidintermediate.factory.ViewModelFactory
import com.d121211069.submissionawalandroidintermediate.viewmodel.SignupViewModel
import com.d121211069.submissionawalandroidintermediate.util.Result

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String
    private val gmailPattern = "[a-zA-Z0-9._-]+@gmail\\.com"

    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val signupViewModel: SignupViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMyButtonEnable()

        binding.emailEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            setMyButtonEnable()
        })

        binding.passwordEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            setMyButtonEnable()
        })

        binding.nameEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            setMyButtonEnable()
        })

        setupAction()
        playAnimation()
    }

    private fun setMyButtonEnable() {
        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString()
        name = binding.nameEditText.text.toString()

        binding.signupButton.isEnabled =
            name.isNotBlank() && password.length >= 8 && email.matches(gmailPattern.toRegex())
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(350)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(350)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(350)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(350)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(350)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(350)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(350)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(350)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            start()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            email = binding.emailEditText.text.toString()
            password = binding.passwordEditText.text.toString()
            name = binding.nameEditText.text.toString()

            if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
                signupViewModel.signupUser(name, email, password).observe(this) { result ->

                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showToast(result.data.message)
                                showLoading(false)

                                finish()
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
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}