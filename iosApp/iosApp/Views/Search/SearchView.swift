import SwiftUI

struct SearchView: View {
    @StateObject private var searchViewModel = SearchViewModel()
    @State private var searchQuery = ""

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Vineyard background image exactly like the mockup
                Image("winemap_bg")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: geometry.size.width, height: geometry.size.height)
                    .clipped()
                    .opacity(0.7) // More transparent to match the mockup

                VStack(spacing: 0) {
                    // Top header with logo - same style as mockup
                    WinemapHeader()

                    Spacer().frame(height: 32)

                    // Search section exactly like mockup
                    HStack(spacing: 12) {
                        // Search field - white background like mockup
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.gray)
                                .font(.system(size: 16))

                            TextField("Find a winery", text: $searchQuery)
                                .font(.system(size: 16))
                                .onSubmit {
                                    // Trigger search in ViewModel
                                    // searchViewModel.searchWineries(searchQuery)
                                }
                        }
                        .padding()
                        .background(Color.white)
                        .cornerRadius(25)
                        .shadow(radius: 1)

                        // Location button - circular white background like mockup
                        Button(action: {
                            // Handle location search
                            // searchViewModel.searchNearby()
                        }) {
                            Image(systemName: "location.fill")
                                .font(.system(size: 24))
                                .foregroundColor(.black)
                                .frame(width: 56, height: 56)
                                .background(Color.white)
                                .clipShape(Circle())
                                .shadow(radius: 1)
                        }
                    }
                    .padding(.horizontal, 32)

                    Spacer()
                }
            }
        }
        .ignoresSafeArea()
    }
}

// SearchViewModel placeholder
class SearchViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var searchResults: [Winery] = []

    // This will be replaced with real implementation from shared module
}

// Preview
struct SearchView_Previews: PreviewProvider {
    static var previews: some View {
        SearchView()
    }
}