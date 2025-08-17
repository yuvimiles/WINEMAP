import SwiftUI

struct ForgotPasswordView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var authViewModel = AuthViewModel()
    @State private var email = ""
    @State private var showSuccess = false

    var body: some View {
        NavigationView {
            VStack(spacing: 32) {
                Spacer()

                if showSuccess {
                    // Success State - Like Android Dialog
                    VStack(spacing: 20) {
                        Text("✅")
                            .font(.system(size: 56))

                        Text("Email Sent!")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.black)

                        Text("We've sent you a link to reset your password. Please check your email.")
                            .font(.system(size: 14))
                            .foregroundColor(.gray)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)

                        Button("OK") {
                            dismiss()
                        }
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color(red: 0.42, green: 0.36, blue: 0.45))
                        .cornerRadius(24)
                        .padding(.horizontal, 32)
                    }
                } else {
                    // Input State - Like Android Dialog
                    VStack(spacing: 20) {
                        // Email Icon - Same as Android
                        Image(systemName: "envelope.fill")
                            .font(.system(size: 56))
                            .foregroundColor(Color(red: 0.42, green: 0.36, blue: 0.45))
                            .padding(.bottom, 8)

                        Text("Forgot Password?")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.black)

                        Text("Enter your email and we'll send you a link to reset your password.")
                            .font(.system(size: 14))
                            .foregroundColor(.gray)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)

                        // Email Field - Same styling as Android
                        TextField("Email", text: $email)
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                            .padding()
                            .frame(height: 50)
                            .background(Color.white)
                            .cornerRadius(12)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                            )
                            .padding(.horizontal, 32)

                        // Buttons Row - Same as Android
                        HStack(spacing: 12) {
                            // Cancel Button
                            Button("Cancel") {
                                dismiss()
                            }
                            .font(.system(size: 16))
                            .foregroundColor(.gray)
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color.clear)
                            .overlay(
                                RoundedRectangle(cornerRadius: 24)
                                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                            )
                            .disabled(authViewModel.isLoading)

                            // Send Button
                            Button(action: sendReset) {
                                HStack {
                                    if authViewModel.isLoading {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                            .scaleEffect(0.8)
                                    } else {
                                        Text("Send")
                                            .font(.system(size: 16, weight: .medium))
                                    }
                                }
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 48)
                                .background(Color(red: 0.42, green: 0.36, blue: 0.45))
                                .cornerRadius(24)
                            }
                            .disabled(email.isEmpty || !email.contains("@") || authViewModel.isLoading)
                            .opacity(email.isEmpty || !email.contains("@") || authViewModel.isLoading ? 0.6 : 1.0)
                        }
                        .padding(.horizontal, 32)
                    }
                }

                Spacer()
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("Reset Password")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium]) // iOS 15+ - Makes it more dialog-like
        .alert("Error", isPresented: .constant(authViewModel.errorMessage != nil)) {
            Button("OK") {
                authViewModel.clearError()
            }
        } message: {
            Text(authViewModel.errorMessage ?? "")
        }
    }

    private func sendReset() {
        authViewModel.sendPasswordReset(email: email) { success in
            if success {
                showSuccess = true
            }
        }
    }
}