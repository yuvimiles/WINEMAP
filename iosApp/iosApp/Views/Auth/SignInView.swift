import SwiftUI

struct SignInView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @State private var email = ""
    @State private var password = ""
    @State private var showForgotPassword = false

    let onNavigateToRegister: () -> Void
    let onNavigateToMain: () -> Void

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Background Image
                Image("winemap_bg")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: geometry.size.width, height: geometry.size.height)
                    .clipped()
                    .opacity(0.9)

                // White overlay
                Color.white.opacity(0.8)
                    .ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 0) {
                        Spacer().frame(height: 50)

                        // Logo
                        Image("winemap_logo")
                            .resizable()
                            .frame(width: 100, height: 100)

                        Spacer().frame(height: 40)

                        // Sign In Title
                        Text("Sign In")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.black)
                            .padding(.bottom, 32)

                        // Form Fields
                        VStack(spacing: 16) {
                            // Email Field
                            CustomTextField(
                                text: $email,
                                placeholder: "Email",
                                keyboardType: .emailAddress
                            )

                            // Password Field
                            CustomSecureField(
                                text: $password,
                                placeholder: "Password"
                            )
                        }
                        .padding(.horizontal, 32)

                        // Forgot Password - Same position as Android
                        HStack {
                            Spacer()
                            Button("forget Password?") {
                                showForgotPassword = true
                            }
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                            .padding(.trailing, 32)
                            .padding(.top, 8)
                        }

                        Spacer().frame(height: 24)

                        // Sign In Button - Same styling as Android
                        Button(action: signIn) {
                            HStack {
                                if authViewModel.isLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                        .scaleEffect(0.8)
                                } else {
                                    Text("Sign In")
                                        .font(.system(size: 16, weight: .medium))
                                        .foregroundColor(.white)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color(red: 0.42, green: 0.36, blue: 0.45)) // 0xFF6B5B73
                            .cornerRadius(24)
                        }
                        .disabled(!isSignInValid || authViewModel.isLoading)
                        .opacity(isSignInValid && !authViewModel.isLoading ? 1.0 : 0.6)
                        .padding(.horizontal, 32)

                        Spacer()

                        // Register Link - Same as Android
                        HStack {
                            Text("Don't have an account?")
                                .font(.system(size: 14))
                                .foregroundColor(.gray)

                            Button("Register") {
                                onNavigateToRegister()
                            }
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.black)
                        }
                        .padding(.bottom, 32)
                    }
                }
            }
        }
        .ignoresSafeArea()
        .sheet(isPresented: $showForgotPassword) {
            ForgotPasswordView()
        }
        .alert("Error", isPresented: .constant(authViewModel.errorMessage != nil)) {
            Button("OK") {
                authViewModel.clearError()
            }
        } message: {
            Text(authViewModel.errorMessage ?? "")
        }
        .onChange(of: authViewModel.isAuthenticated) { isAuthenticated in
            if isAuthenticated {
                onNavigateToMain()
            }
        }
    }

    private var isSignInValid: Bool {
        !email.isEmpty && !password.isEmpty && email.contains("@")
    }

    private func signIn() {
        authViewModel.signIn(email: email, password: password)
    }
}