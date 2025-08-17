import SwiftUI

struct RegisterView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""

    let onNavigateToSignIn: () -> Void
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

                        Spacer().frame(height: 20)

                        // Register Title
                        Text("Register")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.black)
                            .padding(.bottom, 32)

                        // Form Fields - Same spacing as Android
                        VStack(spacing: 16) {
                            // Username Field
                            CustomTextField(
                                text: $username,
                                placeholder: "Username"
                            )

                            // Email Field
                            CustomTextField(
                                text: $email,
                                placeholder: "Email address",
                                keyboardType: .emailAddress
                            )

                            // Password Field
                            CustomSecureField(
                                text: $password,
                                placeholder: "Password"
                            )

                            // Confirm Password Field
                            CustomSecureField(
                                text: $confirmPassword,
                                placeholder: "Confirm Password"
                            )
                        }
                        .padding(.horizontal, 32)

                        Spacer().frame(height: 24)

                        // Sign Up Button - Same styling as Android
                        Button(action: signUp) {
                            HStack {
                                if authViewModel.isLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                        .scaleEffect(0.8)
                                } else {
                                    Text("Sign Up")
                                        .font(.system(size: 16, weight: .medium))
                                        .foregroundColor(.white)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color(red: 0.42, green: 0.36, blue: 0.45)) // 0xFF6B5B73
                            .cornerRadius(24)
                        }
                        .disabled(!isSignUpValid || authViewModel.isLoading)
                        .opacity(isSignUpValid && !authViewModel.isLoading ? 1.0 : 0.6)
                        .padding(.horizontal, 32)

                        Spacer()

                        // Sign In Link - Same as Android
                        HStack {
                            Text("Already have an account?")
                                .font(.system(size: 14))
                                .foregroundColor(.gray)

                            Button("Sign In") {
                                onNavigateToSignIn()
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

    private var isSignUpValid: Bool {
        !username.isEmpty &&
        !email.isEmpty &&
        !password.isEmpty &&
        !confirmPassword.isEmpty &&
        email.contains("@") &&
        password == confirmPassword &&
        password.count >= 6
    }

    private func signUp() {
        authViewModel.signUp(
            username: username,
            email: email,
            password: password,
            confirmPassword: confirmPassword
        )
    }
}