import SwiftUI

struct ProfileView: View {
    @StateObject private var authViewModel = AuthViewModel()
    let onNavigateToEditProfile: () -> Void
    let onNavigateToEditPost: (String) -> Void

    @State private var userPosts: [UserPost] = []

    init(
        onNavigateToEditProfile: @escaping () -> Void = {},
        onNavigateToEditPost: @escaping (String) -> Void = { _ in }
    ) {
        self.onNavigateToEditProfile = onNavigateToEditProfile
        self.onNavigateToEditPost = onNavigateToEditPost
    }

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Same background as other screens
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
                    // Top header with logo
                    WinemapHeader()

                    // Profile section
                    VStack(spacing: 0) {
                        HStack {
                            // Profile picture
                            Image("winemap_logo")
                                .resizable()
                                .frame(width: 80, height: 80)
                                .clipShape(Circle())

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Yuvi_Miles")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.black)

                                Text("love wine")
                                    .font(.system(size: 14))
                                    .foregroundColor(.gray)
                            }

                            Spacer()

                            // Edit button
                            Button(action: onNavigateToEditProfile) {
                                Image(systemName: "pencil")
                                    .font(.system(size: 16))
                                    .foregroundColor(.gray)
                                    .frame(width: 40, height: 40)
                                    .background(Color.gray.opacity(0.2))
                                    .clipShape(Circle())
                            }
                        }
                        .padding(16)

                        // Log out button
                        HStack {
                            Spacer()
                            Button("Log out") {
                                authViewModel.signOut()
                            }
                            .font(.system(size: 14))
                            .foregroundColor(.gray)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color.gray, lineWidth: 1)
                            )
                            .padding(.trailing, 16)
                        }
                    }
                    .background(Color.white.opacity(0.9))

                    // Posts section header
                    HStack {
                        Text("posts")
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(.black)
                        Spacer()
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.white.opacity(0.9))

                    // Posts list
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(userPosts) { post in
                                UserPostCard(
                                    post: post,
                                    onEditClick: { onNavigateToEditPost(post.id) }
                                )
                            }
                        }
                        .padding(16)
                    }
                    .background(Color.white.opacity(0.9))
                }
            }
        }
        .ignoresSafeArea()
        .onAppear {
            loadUserPosts()
        }
    }

    private func loadUserPosts() {
        // Will be loaded from ViewModel in future
        userPosts = []
    }
}

struct UserPostCard: View {
    let post: UserPost
    let onEditClick: () -> Void

    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            // Post image
            Image("winemap_logo")
                .resizable()
                .frame(width: 120, height: 120)
                .cornerRadius(12)
                .clipped()

            VStack(alignment: .leading, spacing: 8) {
                Text(post.wineryName)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.black)

                Text(post.content)
                    .font(.system(size: 14))
                    .foregroundColor(.gray)
                    .lineLimit(3)

                // Star rating
                StarRating(rating: post.rating, size: 14)

                Text(post.timeAgo)
                    .font(.system(size: 12))
                    .foregroundColor(.gray)
            }

            Spacer()

            // Edit button
            Button(action: onEditClick) {
                Image(systemName: "pencil")
                    .font(.system(size: 16))
                    .foregroundColor(.gray)
                    .frame(width: 32, height: 32)
            }
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(16)
        .shadow(radius: 4)
    }
}

// Data model
struct UserPost: Identifiable {
    let id: String
    let imageUrl: String
    let wineryName: String
    let content: String
    let rating: Int
    let timeAgo: String
}