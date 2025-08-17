import SwiftUI

// Custom TextField Component - Same styling as Android
struct CustomTextField: View {
    @Binding var text: String
    let placeholder: String
    var keyboardType: UIKeyboardType = .default

    var body: some View {
        TextField(placeholder, text: $text)
            .keyboardType(keyboardType)
            .autocapitalization(.none)
            .disableAutocorrection(true)
            .padding()
            .frame(height: 50)
            .background(Color.white.opacity(0.9))
            .cornerRadius(25)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
            )
    }
}

// Custom SecureField Component - Same styling as Android
struct CustomSecureField: View {
    @Binding var text: String
    let placeholder: String

    var body: some View {
        SecureField(placeholder, text: $text)
            .autocapitalization(.none)
            .disableAutocorrection(true)
            .padding()
            .frame(height: 50)
            .background(Color.white.opacity(0.9))
            .cornerRadius(25)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
            )
    }
}

// Star Rating Component - Same as Android
struct StarRating: View {
    let rating: Int
    let maxRating: Int
    let size: CGFloat
    let isInteractive: Bool
    let onRatingChanged: (Int) -> Void

    init(
        rating: Int,
        maxRating: Int = 5,
        size: CGFloat = 20,
        isInteractive: Bool = false,
        onRatingChanged: @escaping (Int) -> Void = { _ in }
    ) {
        self.rating = rating
        self.maxRating = maxRating
        self.size = size
        self.isInteractive = isInteractive
        self.onRatingChanged = onRatingChanged
    }

    var body: some View {
        HStack(spacing: 2) {
            ForEach(0..<maxRating, id: \.self) { index in
                let isSelected = index < rating

                if isInteractive {
                    Button(action: {
                        onRatingChanged(index + 1)
                    }) {
                        Text("⭐")
                            .font(.system(size: size * 0.8))
                            .foregroundColor(isSelected ? Color.yellow : Color.gray.opacity(0.5))
                    }
                    .frame(width: size + 8, height: size + 8)
                } else {
                    Text("⭐")
                        .font(.system(size: size * 0.8))
                        .foregroundColor(isSelected ? Color.yellow : Color.gray.opacity(0.5))
                }
            }
        }
    }
}

// Winery Selector Component - Same styling as Android
struct WinerySelector: View {
    @Binding var selectedWinery: String
    let wineryList: [String]
    let onWinerySelected: (String) -> Void

    init(
        selectedWinery: Binding<String>,
        wineryList: [String] = [],
        onWinerySelected: @escaping (String) -> Void = { _ in }
    ) {
        self._selectedWinery = selectedWinery
        self.wineryList = wineryList
        self.onWinerySelected = onWinerySelected
    }

    var body: some View {
        TextField("Choose winery", text: $selectedWinery)
            .onChange(of: selectedWinery) { newValue in
                onWinerySelected(newValue)
            }
            .padding()
            .frame(height: 50)
            .background(Color.white.opacity(0.9))
            .cornerRadius(25)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
            )
    }
}

// Preview for testing
struct CustomTextField_Previews: PreviewProvider {
    @State static var testText = ""
    @State static var testPassword = ""
    @State static var testWinery = ""

    static var previews: some View {
        VStack(spacing: 20) {
            CustomTextField(
                text: $testText,
                placeholder: "Email",
                keyboardType: .emailAddress
            )

            CustomSecureField(
                text: $testPassword,
                placeholder: "Password"
            )

            StarRating(rating: 4, isInteractive: true) { rating in
                print("Selected rating: \(rating)")
            }

            WinerySelector(selectedWinery: $testWinery)
        }
        .padding()
        .background(Color.gray.opacity(0.1))
    }
}