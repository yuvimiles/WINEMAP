import SwiftUI

struct ImagePicker: View {
    let onImageSelected: (String) -> Void
    let modifier: ViewModifier?

    init(
        onImageSelected: @escaping (String) -> Void = { _ in },
        modifier: ViewModifier? = nil
    ) {
        self.onImageSelected = onImageSelected
        self.modifier = modifier
    }

    var body: some View {
        Button(action: {
            // Handle image selection
            // This would typically open camera or gallery
            onImageSelected("selected_image_path")
        }) {
            VStack(spacing: 8) {
                Image(systemName: "photo")
                    .font(.system(size: 48))
                    .foregroundColor(.gray)

                Text("Add Image")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.gray)
            }
            .frame(width: 200, height: 200)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(16)
        }
    }
}

// Alternative version with frame modifier
extension ImagePicker {
    func frame(width: CGFloat, height: CGFloat) -> some View {
        Button(action: {
            onImageSelected("selected_image_path")
        }) {
            VStack(spacing: 8) {
                Image(systemName: "photo")
                    .font(.system(size: min(width, height) * 0.24))
                    .foregroundColor(.gray)

                Text("Add Image")
                    .font(.system(size: min(width, height) * 0.08, weight: .medium))
                    .foregroundColor(.gray)
            }
            .frame(width: width, height: height)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(16)
        }
    }
}