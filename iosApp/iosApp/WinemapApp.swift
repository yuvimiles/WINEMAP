import SwiftUI

@main
struct WinemapApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    @StateObject private var appState = AppState()

    var body: some View {
        Group {
            switch appState.currentScreen {
            case .splash:
                SplashView {
                    appState.navigateToAuth()
                }
            case .signIn:
                SignInView(
                    onNavigateToRegister: {
                        appState.navigateToRegister()
                    },
                    onNavigateToMain: {
                        appState.navigateToMain()
                    }
                )
            case .register:
                RegisterView(
                    onNavigateToSignIn: {
                        appState.navigateToSignIn()
                    },
                    onNavigateToMain: {
                        appState.navigateToMain()
                    }
                )
            case .main:
                MainTabView()
            }
        }
        .animation(.easeInOut(duration: 0.3), value: appState.currentScreen)
    }
}

// MARK: - App State Management
@MainActor
class AppState: ObservableObject {
    @Published var currentScreen: AppScreen = .splash

    func navigateToAuth() {
        currentScreen = .signIn
    }

    func navigateToSignIn() {
        currentScreen = .signIn
    }

    func navigateToRegister() {
        currentScreen = .register
    }

    func navigateToMain() {
        currentScreen = .main
    }
}

enum AppScreen {
    case splash
    case signIn
    case register
    case main
}