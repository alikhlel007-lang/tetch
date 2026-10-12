package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CafeRepository
import com.example.model.AuthMode
import com.example.ui.components.CafeLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    repository: CafeRepository,
    onAuthSuccess: () -> Unit,
    onSkipToCustomerMenu: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var emailOrUsername by remember { mutableStateOf("cashier@cafe.com") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("12345678") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF02171A),
                        BackgroundDark,
                        Color(0xFF031012)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Signature CAFE Logo from uploaded screenshot #2 & #3
            CafeLogo(iconSize = 78.dp, showText = true, titleSize = 28)

            Spacer(modifier = Modifier.height(28.dp))

            // Main Card Container with curved teal borders
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardDark.copy(alpha = 0.95f))
                    .border(
                        width = 1.dp,
                        color = CardBorder,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (mode) {
                        AuthMode.LOGIN -> {
                            Text(
                                text = "Login Account",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Username / Email input
                            Text(
                                text = "Username",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                placeholder = { Text("Email or Username", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = CyanPrimary)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_email_input")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Password input
                            Text(
                                text = "Password",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("••••••••", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = CyanPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_password_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Remember Me & Forgot Password row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CyanPrimary,
                                            uncheckedColor = CardBorder,
                                            checkmarkColor = Color(0xFF041316)
                                        )
                                    )
                                    Text(
                                        text = "Remember Me",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = "Forgot Password?",
                                    color = CyanPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.clickable { mode = AuthMode.FORGOT_PASSWORD }
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Login Button
                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        val result = repository.login(emailOrUsername, password)
                                        isLoading = false
                                        if (result.isSuccess) {
                                            onAuthSuccess()
                                        } else {
                                            snackbarHostState.showSnackbar("فشل تسجيل الدخول: تحقق من البيانات")
                                        }
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary,
                                    contentColor = Color(0xFF031619)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("auth_login_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color(0xFF031619),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Login",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Don't have an account?",
                                color = TextMuted,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Create Account Outline Button
                            OutlinedButton(
                                onClick = { mode = AuthMode.REGISTER },
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyanPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = CyanPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Create Account",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        AuthMode.REGISTER -> {
                            Text(
                                text = "Create Account",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Full Name / الإسم الكامل
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Full Name", color = TextSecondary, fontSize = 12.sp)
                                Text("الإسم الكامل", color = TextMuted, fontSize = 12.sp)
                            }
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                placeholder = { Text("e.g., John Doe", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Email / البريد الإلكتروني
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Email", color = TextSecondary, fontSize = 12.sp)
                                Text("البريد الإلكتروني", color = TextMuted, fontSize = 12.sp)
                            }
                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                placeholder = { Text("e.g., john.doe@mail.com", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password / كلمة السر
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Password", color = TextSecondary, fontSize = 12.sp)
                                Text("كلمة السر", color = TextMuted, fontSize = 12.sp)
                            }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("At least 8 characters", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanPrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Password / تأكيد كلمة السر
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Confirm Password", color = TextSecondary, fontSize = 12.sp)
                                Text("تأكيد كلمة السر", color = TextMuted, fontSize = 12.sp)
                            }
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                placeholder = { Text("••••••••", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanPrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = TextSecondary)
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "By continuing, you agree to our Terms & Privacy\nباستمرارك، أنت توافق على الشروط والأحكام والخصوصية",
                                color = TextMuted,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        val result = repository.register(fullName, emailOrUsername, password)
                                        isLoading = false
                                        if (result.isSuccess) {
                                            onAuthSuccess()
                                        } else {
                                            snackbarHostState.showSnackbar("فشل إنشاء الحساب")
                                        }
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary,
                                    contentColor = Color(0xFF031619)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("إنشاء حساب", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Already have an account? / لديك حساب بالفعل؟",
                                color = TextMuted,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { mode = AuthMode.LOGIN },
                                shape = CircleShape,
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyanPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("تسجيل الدخول", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }

                        AuthMode.FORGOT_PASSWORD -> {
                            Text(
                                text = "Forgot Password",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Enter your email address to receive a password reset link.\nأدخل بريدك الإلكتروني لتلقي رابط إعادة تعيين كلمة السر",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Email Address", color = TextSecondary, fontSize = 12.sp)
                                Text("عنوان البريد الإلكتروني", color = TextMuted, fontSize = 12.sp)
                            }
                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                placeholder = { Text("e.g., john.doe@mail.com", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = Color(0xFF071C21),
                                    unfocusedContainerColor = Color(0xFF071C21)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        repository.sendPasswordReset(emailOrUsername)
                                        isLoading = false
                                        snackbarHostState.showSnackbar("تم إرسال رابط إعادة التعيين إلى بريدك")
                                        mode = AuthMode.LOGIN
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyanPrimary,
                                    contentColor = Color(0xFF031619)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("إرسال رابط إعادة تعيين", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(onClick = { mode = AuthMode.LOGIN }) {
                                Text("العودة لتسجيل الدخول", color = CyanPrimary, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick bypass to customer QR menu directly (for previewing menu as customer)
            TextButton(
                onClick = onSkipToCustomerMenu,
                modifier = Modifier.testTag("preview_customer_menu_button")
            ) {
                Text(
                    text = "تجربة قائمة الزبون (مسح QR الطاولة) ↗",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
