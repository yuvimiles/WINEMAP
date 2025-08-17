import SwiftUI
import Combine
import shared

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isAuthenticated = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var currentUser: User?

    @Published var signInEmail = ""
    @Published var signInPassword = ""
    @Published var signUpUsername = ""
    @Published var signUpEmail = ""
    @Published var signUpPassword = ""
    @Published var signUpConfirmPassword = ""
    @Published var showForgotPasswordDialog = false
    @Published var forgotPasswordEmail = ""
    @Published var isForgotPasswordLoading = false
    @Published var forgotPasswordMessage: String?

    private var cancellables = Set<AnyCancellable>()
    private let sharedAuthViewModel: shared.AuthViewModel
    private var stateObserver: Kotlinx_coroutines_coreDisposableHandle?

    init() {
        self.sharedAuthViewModel = ViewModelProvider.companion.authViewModel
        observeSharedState()
    }

    private func observeSharedState() {
        stateObserver = sharedAuthViewModel.uiState.watch { [weak self] authUiState in
            guard let self = self,
                  let state = authUiState as? AuthUiState else { return }

            DispatchQueue.main.async {
                self.updateFromSharedState(state)
            }
        }
    }

    private func updateFromSharedState(_ state: AuthUiState) {
        self.isAuthenticated = state.isAuthenticated
        self.isLoading = state.isLoading
        self.errorMessage = state.errorMessage
        self.currentUser = state.currentUser
        self.signInEmail = state.signInEmail
        self.signInPassword = state.signInPassword
        self.signUpUsername = state.signUpUsername
        self.signUpEmail = state.signUpEmail
        self.signUpPassword = state.signUpPassword
        self.signUpConfirmPassword = state.signUpConfirmPassword
        self.showForgotPasswordDialog = state.showForgotPasswordDialog
        self.forgotPasswordEmail = state.forgotPasswordEmail
        self.isForgotPasswordLoading = state.isForgotPasswordLoading
        self.forgotPasswordMessage = state.forgotPasswordMessage
    }

    func signIn(email: String? = nil, password: String? = nil) {
        let emailToUse = email ?? signInEmail
        let passwordToUse = password ?? signInPassword

        sharedAuthViewModel.updateSignInEmail(email: emailToUse)
        sharedAuthViewModel.updateSignInPassword(password: passwordToUse)
        sharedAuthViewModel.signIn()
    }

    func signUp(username: String? = nil, email: String? = nil, password: String? = nil, confirmPassword: String? = nil) {
        let usernameToUse = username ?? signUpUsername
        let emailToUse = email ?? signUpEmail
        let passwordToUse = password ?? signUpPassword
        let confirmPasswordToUse = confirmPassword ?? signUpConfirmPassword

        sharedAuthViewModel.updateSignUpUsername(username: usernameToUse)
        sharedAuthViewModel.updateSignUpEmail(email: emailToUse)
        sharedAuthViewModel.updateSignUpPassword(password: passwordToUse)
        sharedAuthViewModel.updateSignUpConfirmPassword(confirmPassword: confirmPasswordToUse)
        sharedAuthViewModel.signUp()
    }

    func sendPasswordReset(email: String, completion: @escaping (Bool) -> Void) {
        sharedAuthViewModel.updateForgotPasswordEmail(email: email)
        sharedAuthViewModel.sendPasswordReset()

        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            let success = self.forgotPasswordMessage != nil && self.errorMessage == nil
            completion(success)
        }
    }

    func showForgotPasswordDialog() {
        sharedAuthViewModel.showForgotPasswordDialog()
    }

    func hideForgotPasswordDialog() {
        sharedAuthViewModel.hideForgotPasswordDialog()
    }

    func updateForgotPasswordEmail(_ email: String) {
        sharedAuthViewModel.updateForgotPasswordEmail(email: email)
    }

    func signOut() {
        sharedAuthViewModel.signOut()
    }

    func clearError() {
        sharedAuthViewModel.clearError()
    }

    var isSignInValid: Bool {
        return sharedAuthViewModel.uiState.value.isSignInValid
    }

    var isSignUpValid: Bool {
        return sharedAuthViewModel.uiState.value.isSignUpValid
    }

    var currentUserName: String {
        return currentUser?.username ?? ""
    }

    var currentUserEmail: String {
        return currentUser?.email ?? ""
    }

    deinit {
        stateObserver?.dispose()
        sharedAuthViewModel.onCleared()
    }
}