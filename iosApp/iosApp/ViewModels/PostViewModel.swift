import SwiftUI
import Combine
import shared

@MainActor
class PostViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var posts: [Post] = []
    @Published var userPosts: [UserPost] = []
    @Published var errorMessage: String?

    // Create Post Properties
    @Published var selectedWinery = ""
    @Published var postContent = ""
    @Published var postRating = 0
    @Published var selectedImagePath = ""

    // Private properties
    private var cancellables = Set<AnyCancellable>()


    private let sharedPostViewModel: shared.PostViewModel
    private var stateObserver: Kotlinx_coroutines_coreDisposableHandle?

    init() {
        self.sharedPostViewModel = ViewModelProvider.companion.postViewModel
        observeSharedState()
    }

    private func observeSharedState() {
        stateObserver = sharedPostViewModel.uiState.watch { [weak self] postUiState in
            guard let self = self,
                  let state = postUiState as? PostUiState else { return }

            DispatchQueue.main.async {
                self.updateFromSharedState(state)
            }
        }
    }

    private func updateFromSharedState(_ state: PostUiState) {
        self.isLoading = state.isLoading
        self.errorMessage = state.errorMessage

        // Convert Kotlin Posts to iOS Posts
        self.posts = state.posts.compactMap { kotlinPost in
            guard let post = kotlinPost as? shared.Post else { return nil }
            return Post(
                id: post.id,
                content: post.content,
                rating: Int(post.rating),
                userId: post.userId,
                wineryId: post.wineryName,
                imageUrl: post.imageUrl,
                createdAt: Date(timeIntervalSince1970: TimeInterval(post.timestamp) / 1000)
            )
        }

        // Update form fields from shared state
        self.selectedWinery = state.newPostWinery
        self.postContent = state.newPostContent
        self.postRating = Int(state.newPostRating)
        self.selectedImagePath = state.newPostImagePath ?? ""
    }

    // MARK: - Load Posts
    func loadPosts() {
        // הshared module כבר טוען posts אוטומטית
        // אבל אנחנו יכולים לטעון posts ספציפיים אם צריך
    }

    // MARK: - Load User Posts
    func loadUserPosts(userId: String? = nil) {
        if let userId = userId {
            sharedPostViewModel.loadUserPosts(userId: userId)
        }
    }

    // MARK: - Load Winery Posts
    func loadWineryPosts(wineryName: String) {
        sharedPostViewModel.loadWineryPosts(wineryName: wineryName)
    }

    // MARK: - Create Post
    func createPost(winery: String? = nil, content: String? = nil, rating: Int? = nil, imagePath: String? = nil) {
        let wineryToUse = winery ?? selectedWinery
        let contentToUse = content ?? postContent
        let ratingToUse = rating ?? postRating
        let imagePathToUse = imagePath ?? selectedImagePath.isEmpty ? nil : selectedImagePath

        sharedPostViewModel.updateNewPostWinery(winery: wineryToUse)
        sharedPostViewModel.updateNewPostContent(content: contentToUse)
        sharedPostViewModel.updateNewPostRating(rating: Int32(ratingToUse))
        if let imagePathToUse = imagePathToUse, !imagePathToUse.isEmpty {
            sharedPostViewModel.updateNewPostImage(imagePath: imagePathToUse)
        }

        sharedPostViewModel.createPost()
    }

    // MARK: - Update Post
    func updatePost(postId: String, content: String, rating: Int) {
        guard !content.isEmpty, rating > 0 else {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        errorMessage = nil

        // Mock success for now - יוחלף בעתיד
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.isLoading = false
        }
    }

    // MARK: - Delete Post
    func deletePost(postId: String) {

        isLoading = true
        errorMessage = nil

        // Mock success for now - יוחלף בעתיד
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.isLoading = false
            self.removePostFromList(postId)
        }
    }

    // MARK: - Update Form Fields (חדש)
    func updateSelectedWinery(_ winery: String) {
        selectedWinery = winery
        sharedPostViewModel.updateNewPostWinery(winery: winery)
    }

    func updatePostContent(_ content: String) {
        postContent = content
        sharedPostViewModel.updateNewPostContent(content: content)
    }

    func updatePostRating(_ rating: Int) {
        postRating = rating
        sharedPostViewModel.updateNewPostRating(rating: Int32(rating))
    }

    func updateSelectedImagePath(_ imagePath: String) {
        selectedImagePath = imagePath
        sharedPostViewModel.updateNewPostImage(imagePath: imagePath.isEmpty ? nil : imagePath)
    }

    // MARK: - Helper Methods
    private func clearCreatePostFields() {
        // הshared module יטפל בזה
        sharedPostViewModel.resetCreatePostForm()
    }

    private func updatePostInList(_ updatedPost: Post) {
        if let index = posts.firstIndex(where: { $0.id == updatedPost.id }) {
            posts[index] = updatedPost
        }
    }

    private func removePostFromList(_ postId: String) {
        posts.removeAll { $0.id == postId }
        userPosts.removeAll { $0.id == postId }
    }

    // MARK: - Error Handling
    func clearError() {
        sharedPostViewModel.clearAuthError()

    // MARK: - Validation
    var isCreatePostValid: Bool {
        return sharedPostViewModel.uiState.value.isCreatePostValid
    }

    deinit {
        stateObserver?.dispose()
        sharedPostViewModel.onCleared()
    }
}

// MARK: - Data Models
struct Post: Identifiable {
    let id: String
    let content: String
    let rating: Int
    let userId: String
    let wineryId: String
    let imageUrl: String
    let createdAt: Date
}

// MARK: - UserPost Model (הוסף לתאימות)
struct UserPost: Identifiable {
    let id: String
    let imageUrl: String
    let wineryName: String
    let content: String
    let rating: Int
    let timeAgo: String

    // Convert from regular Post
    init(from post: Post, wineryName: String) {
        self.id = post.id
        self.imageUrl = post.imageUrl
        self.wineryName = wineryName
        self.content = post.content
        self.rating = post.rating

        // Calculate time ago
        let now = Date()
        let diff = now.timeIntervalSince(post.createdAt)

        if diff < 60 {
            self.timeAgo = "now"
        } else if diff < 3600 {
            self.timeAgo = "\(Int(diff / 60))m ago"
        } else if diff < 86400 {
            self.timeAgo = "\(Int(diff / 3600))h ago"
        } else {
            self.timeAgo = "\(Int(diff / 86400))d ago"
        }
    }
}