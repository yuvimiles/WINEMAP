import SwiftUI
import Combine
import shared

@MainActor
class ProfileViewModel: ObservableObject {

    // MARK: - Published Properties

    // User Info
    @Published var currentUser: User?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isAuthenticated = false

    // Profile Data
    @Published var displayName = ""
    @Published var bio = ""
    @Published var profileImageUrl = ""
    @Published var joinDate = ""
    @Published var favoriteWineries: [String] = []

    // User Posts
    @Published var userPosts: [UserPost] = []
    @Published var isLoadingPosts = false
    @Published var postsErrorMessage: String?

    // Edit Mode
    @Published var isEditing = false
    @Published var editingDisplayName = ""
    @Published var editingBio = ""
    @Published var editingProfileImage = ""
    @Published var showImagePicker = false
    @Published var isSavingProfile = false

    // Statistics
    @Published var totalPosts = 0
    @Published var totalWineriesVisited = 0
    @Published var averageRating: Float = 0.0
    @Published var favoriteWineriesCount = 0

    // Navigation
    @Published var selectedPost: UserPost?
    @Published var showEditPost = false
    @Published var showDeleteConfirmation = false
    @Published var postToDelete: UserPost?

    // MARK: - Private Properties
    private var cancellables = Set<AnyCancellable>()

    // Shared ViewModels
    private let sharedAuthViewModel: shared.AuthViewModel
    private let sharedPostViewModel: shared.PostViewModel
    private var authStateObserver: Kotlinx_coroutines_coreDisposableHandle?
    private var postStateObserver: Kotlinx_coroutines_coreDisposableHandle?

    // MARK: - Initialization
    init() {
        self.sharedAuthViewModel = ViewModelProvider.companion.authViewModel
        self.sharedPostViewModel = ViewModelProvider.companion.postViewModel

        observeSharedState()
        loadUserProfile()
    }

    // MARK: - Observe Shared State
    private func observeSharedState() {
        // Observe Auth State
        authStateObserver = sharedAuthViewModel.uiState.watch { [weak self] authUiState in
            guard let self = self,
                  let state = authUiState as? AuthUiState else { return }

            DispatchQueue.main.async {
                self.updateFromAuthState(state)
            }
        }

        // Observe Post State
        postStateObserver = sharedPostViewModel.uiState.watch { [weak self] postUiState in
            guard let self = self,
                  let state = postUiState as? PostUiState else { return }

            DispatchQueue.main.async {
                self.updateFromPostState(state)
            }
        }
    }

    private func updateFromAuthState(_ state: AuthUiState) {
        self.isAuthenticated = state.isAuthenticated
        self.currentUser = state.currentUser
        self.isLoading = state.isLoading
        self.errorMessage = state.errorMessage

        // Update profile data from user
        if let user = state.currentUser {
            updateProfileData(from: user)
        }
    }

    private func updateFromPostState(_ state: PostUiState) {
        self.isLoadingPosts = state.isLoading
        self.postsErrorMessage = state.errorMessage

        // Convert posts to UserPosts
        updateUserPosts(from: state.posts)
        updateStatistics(from: state.posts)
    }

    // MARK: - Profile Data Management
    private func updateProfileData(from user: User) {
        displayName = user.username
        bio = user.bio
        profileImageUrl = user.profileImageUrl
        favoriteWineries = user.favoriteWineries
        favoriteWineriesCount = user.favoriteWineries.count

        // Calculate join date (mock for now)
        joinDate = "Member since 2024"

        // Update editing fields
        editingDisplayName = displayName
        editingBio = bio
        editingProfileImage = profileImageUrl
    }

    private func updateUserPosts(from posts: [shared.Post]) {
        guard let currentUserId = currentUser?.id else { return }

        // Filter posts by current user and convert to UserPost
        userPosts = posts.compactMap { kotlinPost in
            guard kotlinPost.userId == currentUserId else { return nil }

            return UserPost(
                id: kotlinPost.id,
                imageUrl: kotlinPost.imageUrl,
                wineryName: kotlinPost.wineryName,
                content: kotlinPost.content,
                rating: Int(kotlinPost.rating),
                timeAgo: formatTimeAgo(from: kotlinPost.timestamp)
            )
        }.sorted { $0.timeAgo < $1.timeAgo } // Sort by newest first
    }

    private func updateStatistics(from posts: [shared.Post]) {
        guard let currentUserId = currentUser?.id else { return }

        let userPostsList = posts.filter { $0.userId == currentUserId }

        totalPosts = userPostsList.count

        // Calculate unique wineries visited
        let uniqueWineries = Set(userPostsList.map { $0.wineryName })
        totalWineriesVisited = uniqueWineries.count

        // Calculate average rating
        if !userPostsList.isEmpty {
            let totalRating = userPostsList.reduce(0) { $0 + Float($1.rating) }
            averageRating = totalRating / Float(userPostsList.count)
        } else {
            averageRating = 0.0
        }
    }

    private func formatTimeAgo(from timestamp: Int64) -> String {
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let diff = now - timestamp

        switch diff {
        case 0..<60_000:
            return "now"
        case 60_000..<3_600_000:
            return "\(diff / 60_000)m ago"
        case 3_600_000..<86_400_000:
            return "\(diff / 3_600_000)h ago"
        default:
            return "\(diff / 86_400_000)d ago"
        }
    }

    // MARK: - Profile Actions
    func loadUserProfile() {
        guard let userId = currentUser?.id else { return }
        loadUserPosts(userId: userId)
    }

    func refreshProfile() {
        loadUserProfile()
    }

    func loadUserPosts(userId: String) {
        sharedPostViewModel.loadUserPosts(userId: userId)
    }

    // MARK: - Edit Profile
    func startEditing() {
        isEditing = true
        editingDisplayName = displayName
        editingBio = bio
        editingProfileImage = profileImageUrl
    }

    func cancelEditing() {
        isEditing = false
        editingDisplayName = displayName
        editingBio = bio
        editingProfileImage = profileImageUrl
    }

    func saveProfile() {
        guard let currentUser = currentUser else { return }

        isSavingProfile = true

        // Create updated user
        let updatedUser = User(
            id: currentUser.id,
            username: editingDisplayName,
            email: currentUser.email,
            profileImageUrl: editingProfileImage,
            bio: editingBio,
            favoriteWineries: currentUser.favoriteWineries
        )

        // TODO: Call shared module to update user
        // For now, simulate save
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.isSavingProfile = false
            self.isEditing = false

            // Update local data
            self.displayName = self.editingDisplayName
            self.bio = self.editingBio
            self.profileImageUrl = self.editingProfileImage
        }
    }

    func updateProfileImage(_ imagePath: String) {
        editingProfileImage = imagePath

        // TODO: Upload image to Firebase Storage through shared module
        // For now, just update locally
    }

    // MARK: - Image Picker
    func openImagePicker() {
        showImagePicker = true
    }

    func handleImageSelected(_ imagePath: String) {
        updateProfileImage(imagePath)
        showImagePicker = false
    }

    // MARK: - Favorites Management
    func addFavoriteWinery(_ wineryName: String) {
        guard let currentUser = currentUser,
              !currentUser.favoriteWineries.contains(wineryName) else { return }

        favoriteWineries.append(wineryName)
        favoriteWineriesCount += 1

        // TODO: Update through shared module
    }

    func removeFavoriteWinery(_ wineryName: String) {
        guard let currentUser = currentUser else { return }

        favoriteWineries.removeAll { $0 == wineryName }
        favoriteWineriesCount = favoriteWineries.count

        // TODO: Update through shared module
    }

    func isFavoriteWinery(_ wineryName: String) -> Bool {
        return favoriteWineries.contains(wineryName)
    }

    // MARK: - Post Management
    func selectPost(_ post: UserPost) {
        selectedPost = post
    }

    func editPost(_ post: UserPost) {
        selectedPost = post
        showEditPost = true
    }

    func confirmDeletePost(_ post: UserPost) {
        postToDelete = post
        showDeleteConfirmation = true
    }

    func deletePost() {
        guard let post = postToDelete else { return }

        // TODO: Call shared module to delete post
        // For now, remove from local array
        userPosts.removeAll { $0.id == post.id }
        totalPosts -= 1

        // Update statistics
        if let user = currentUser {
            let remainingPosts = userPosts
            totalWineriesVisited = Set(remainingPosts.map { $0.wineryName }).count

            if !remainingPosts.isEmpty {
                let totalRating = remainingPosts.reduce(0) { $0 + Float($1.rating) }
                averageRating = totalRating / Float(remainingPosts.count)
            } else {
                averageRating = 0.0
            }
        }

        postToDelete = nil
        showDeleteConfirmation = false
    }

    func cancelDeletePost() {
        postToDelete = nil
        showDeleteConfirmation = false
    }

    // MARK: - Sign Out
    func signOut() {
        sharedAuthViewModel.signOut()
        clearLocalData()
    }

    private func clearLocalData() {
        currentUser = nil
        displayName = ""
        bio = ""
        profileImageUrl = ""
        favoriteWineries = []
        userPosts = []
        totalPosts = 0
        totalWineriesVisited = 0
        averageRating = 0.0
        favoriteWineriesCount = 0
        isEditing = false
    }

    // MARK: - Error Handling
    func clearError() {
        errorMessage = nil
        sharedAuthViewModel.clearError()
    }

    func clearPostsError() {
        postsErrorMessage = nil
        sharedPostViewModel.clearAuthError()
    }

    // MARK: - Computed Properties
    var hasProfileImage: Bool {
        return !profileImageUrl.isEmpty
    }

    var hasPosts: Bool {
        return !userPosts.isEmpty
    }

    var canEdit: Bool {
        return currentUser != nil && isAuthenticated
    }

    var isProfileComplete: Bool {
        return !displayName.isEmpty && !bio.isEmpty
    }

    var formattedAverageRating: String {
        return String(format: "%.1f", averageRating)
    }

    // Validation for edit form
    var isEditFormValid: Bool {
        return !editingDisplayName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    // MARK: - Cleanup
    deinit {
        authStateObserver?.dispose()
        postStateObserver?.dispose()
        sharedAuthViewModel.onCleared()
        sharedPostViewModel.onCleared()
    }
}

// MARK: - ProfileViewModel Extensions

extension ProfileViewModel {

    // MARK: - Statistics Helpers
    func getPostsCount(for wineryName: String) -> Int {
        return userPosts.filter { $0.wineryName == wineryName }.count
    }

    func getAverageRating(for wineryName: String) -> Float {
        let wineryPosts = userPosts.filter { $0.wineryName == wineryName }
        guard !wineryPosts.isEmpty else { return 0.0 }

        let totalRating = wineryPosts.reduce(0) { $0 + Float($1.rating) }
        return totalRating / Float(wineryPosts.count)
    }

    func getMostVisitedWinery() -> String? {
        guard !userPosts.isEmpty else { return nil }

        let wineryCounts = Dictionary(grouping: userPosts) { $0.wineryName }
            .mapValues { $0.count }

        return wineryCounts.max(by: { $0.value < $1.value })?.key
    }

    func getRecentlyVisitedWineries(limit: Int = 5) -> [String] {
        let recentPosts = userPosts.prefix(limit)
        let uniqueWineries = Array(Set(recentPosts.map { $0.wineryName }))
        return Array(uniqueWineries.prefix(limit))
    }
}

// MARK: - Preview Helper
extension ProfileViewModel {
    static func preview() -> ProfileViewModel {
        let viewModel = ProfileViewModel()

        // Mock user data for preview
        viewModel.displayName = "Yuvi_Miles"
        viewModel.bio = "love wine"
        viewModel.totalPosts = 12
        viewModel.totalWineriesVisited = 8
        viewModel.averageRating = 4.2
        viewModel.favoriteWineriesCount = 5

        // Mock posts
        viewModel.userPosts = [
            UserPost(
                id: "1",
                imageUrl: "",
                wineryName: "יקב ספרה",
                content: "מקום מדהים עם יינות מצויינים!",
                rating: 5,
                timeAgo: "2h ago"
            ),
            UserPost(
                id: "2",
                imageUrl: "",
                wineryName: "יקב גליל",
                content: "חוויה נפלאה בגליל העליון",
                rating: 4,
                timeAgo: "1d ago"
            )
        ]

        return viewModel
    }
}