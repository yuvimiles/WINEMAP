import SwiftUI

struct EditPostView: View {
    @StateObject private var postViewModel = PostViewModel()
    @State private var content = ""
    @State private var rating = 5

    let postId: String
    let onNavigateBack: () -> Void
    let onPostUpdated: () -> Void
    let onPostDeleted: () -> Void

    init(
        postId: String = "",
        onNavigateBack: @escaping () -> Void = {},
        onPostUpdated: @escaping () -> Void = {},
        onPostDeleted: @escaping () -> Void = {}
    ) {
        self.postId = postId
        self.onNavigateBack = onNavigateBack
        self.onPostUpdated = onPostUpdated
        self.onPostDeleted = onPostDeleted
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
                    // Top header
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

                        Button("") {}
                            .opacity(0)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)
                    .background(Color(red: 0.91, green: 0.86, blue: 0.78))

                    // Edit post title
                    Text("Edit post")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.black)
                        .padding(16)
                        .frame(maxWidth: .infinity)
                        .background(Color.white.opacity(0.9))

                    // Edit form
                    VStack(spacing: 16) {
                        Spacer().frame(height: 16)

                        // Post image
                        Image("winemap_logo")
                            .resizable()
                            .frame(width: 200, height: 200)
                            .cornerRadius(16)
                            .clipped()

                        Spacer().frame(height: 24)

                        // Content field
                        TextField("", text: $content, axis: .vertical)
                            .lineLimit(5)
                            .multilineTextAlignment(.center)
                            .padding()
                            .frame(height: 120)
                            .background(Color.white.opacity(0.9))
                            .cornerRadius(8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                            )
                            .padding(.horizontal, 16)

                        Spacer().frame(height: 24)

                        // Star rating
                        StarRating(
                            rating: rating,
                            maxRating: 5,
                            size: 24,
                            isInteractive: true,
                            onRatingChanged: { newRating in
                                rating = newRating
                            }
                        )

                        Spacer()

                        // Action buttons
                        VStack(spacing: 8) {
                            // Save button
                            Button(action: savePost) {
                                Text("Save")
                                    .font(.system(size: 14))
                                    .foregroundColor(.white)
                                    .frame(width: 120, height: 40)
                                    .background(Color(red: 0.55, green: 0.44, blue: 0.28))
                                    .cornerRadius(20)
                            }

                            // Delete button
                            Button(action: deletePost) {
                                Text("delete post")
                                    .font(.system(size: 14))
                                    .foregroundColor(.red)
                            }
                        }

                        Spacer().frame(height: 16)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.white.opacity(0.9))
                }
            }
        }
        .ignoresSafeArea()
        .onAppear {
            loadPostData()
        }
    }

    private func loadPostData() {
        // Load post data from ViewModel
        // content = postData.content
        // rating = postData.rating
    }

    private func savePost() {
        // Save post with postViewModel
        // postViewModel.updatePost(postId, content, rating)
        onPostUpdated()
    }

    private func deletePost() {
        // Delete post with postViewModel
        // postViewModel.deletePost(postId)
        onPostDeleted()
    }
}