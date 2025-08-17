import SwiftUI

struct EditProfileView: View {
    @StateObject private var authViewModel = AuthViewModel()
    @State private var username = "Yuvi_Miles"
    @State private var bio = "love wine"

    let onNavigateBack: () -> Void
    let onSaveProfile: (String, String) -> Void

    init(
        onNavigateBack: @escaping () -> Void = {},
        onSaveProfile: @escaping (String, String) -> Void = { _, _ in }
    ) {
        self.onNavigateBack = onNavigateBack
        self.onSaveProfile = onSaveProfile
    }

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Same background
                Image("winemap_bg")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: geometry.size.width, height: geometry.size.height)
                    .clipped()
                    .opacity(0.9)

                // White overlay
                Color.white.opacity(0.8)
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    // Top header with back arrow
                    HStack {
                        Button(action: onNavigateBack) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 18, weight: .medium))
                                .foregroundColor(.black)
                        }

                        Spacer()

                        HStack(spacing: 8) {
                            Image("winemap_logo")
                                .resizable()
                                .frame(width: 40, height: 40)

                            Text("WINEMAP")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(.black)
                        }

                        Spacer()

                        // Invisible spacer for centering
                        Button("") {}
                            .opacity(0)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)
                    .background(Color(red: 0.91, green: 0.86, blue: 0.78)) // 0xFFE8DCC6

                    // Edit Profile title
                    Text("Edit profile")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.black)
                        .padding(16)
                        .frame(maxWidth: .infinity)
                        .background(Color.white.opacity(0.9))

                    // Edit form
                    VStack(spacing: 16) {
                        Spacer().frame(height: 32)

                        // Profile picture
                        Image("winemap_logo") // Replace with actual profile image
                            .resizable()
                            .frame(width: 100, height: 100)
                            .clipShape(Circle())

                        Spacer().frame(height: 32)

                        // Username field
                        TextField("Username", text: $username)
                            .padding()
                            .frame(height: 50)
                            .background(Color.white.opacity(0.9))
                            .cornerRadius(8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                            )

                        // Bio field
                        TextField("Bio", text: $bio)
                            .padding()
                            .frame(height: 50)
                            .background(Color.white.opacity(0.9))
                            .cornerRadius(8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                            )

                        Spacer()

                        // Save button
                        Button(action: {
                            onSaveProfile(username, bio)
                        }) {
                            Text("Save")
                                .font(.system(size: 14))
                                .foregroundColor(.white)
                                .frame(width: 120, height: 40)
                                .background(Color(red: 0.55, green: 0.44, blue: 0.28)) // 0xFF8B6F47
                                .cornerRadius(20)
                        }

                        Spacer().frame(height: 16)
                    }
                    .padding(.horizontal, 16)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.white.opacity(0.9))
                }
            }
        }
        .ignoresSafeArea()
    }
}