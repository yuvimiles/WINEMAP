import SwiftUI

struct PostCard: View {
    let userImage: String
    let userName: String
    let timeAgo: String
    let postImage: String
    let postContent: String
    let rating: Int
    let showEditButton: Bool
    let onEditClick: () -> Void

    init(
        userImage: String = "",
        userName: String,
        timeAgo: String,
        postImage: String = "",
        postContent: String,
        rating: Int = 5,
        showEditButton: Bool = false,
        onEditClick: @escaping () -> Void = {}
    ) {
        self.userImage = userImage
        self.userName = userName
        self.timeAgo = timeAgo
        self.postImage = postImage
        self.postContent = postContent
        self.rating = rating
        self.showEditButton = showEditButton
        self.onEditClick = onEditClick
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // User info header
            HStack {
                // User profile image
                Image("winemap_logo") // Replace with actual user image
                    .resizable()
                    .frame(width: 40, height: 40)
                    .clipShape(Circle())

                VStack(alignment: .leading, spacing: 2) {
                    Text(userName)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.black)

                    Text(timeAgo)
                        .font(.system(size: 12))
                        .foregroundColor(.gray)
                }

                Spacer()

                // Edit button (only for user's own posts)
                if showEditButton {
                    Button(action: onEditClick) {
                        Image(systemName: "pencil")
                            .font(.system(size: 16))
                            .foregroundColor(.gray)
                            .frame(width: 32, height: 32)
                    }
                }
            }

            // Post image
            Image("winemap_logo") // Replace with actual post image
                .resizable()
                .frame(height: 200)
                .cornerRadius(12)
                .clipped()

            // Post content
            Text(postContent)
                .font(.system(size: 14))
                .foregroundColor(.black)
                .lineSpacing(6)

            // Star rating
            StarRating(rating: rating, size: 16)
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(16)
        .shadow(radius: 4)
        .padding(.vertical, 8)
    }
}