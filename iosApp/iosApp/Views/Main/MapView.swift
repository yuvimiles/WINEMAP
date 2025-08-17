import SwiftUI
import MapKit

struct MapView: View {
    @StateObject private var postViewModel = PostViewModel()
    @State private var selectedWinery: Winery?
    @State private var wineries: [Winery] = []
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 32.7767, longitude: 35.0231), // Israel center
        span: MKCoordinateSpan(latitudeDelta: 2.0, longitudeDelta: 2.0)
    )

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // Top header with logo - exactly like Android
                WinemapHeader()

                // Map area - Real Google Maps equivalent with MapKit
                Map(coordinateRegion: $region, annotationItems: wineries) { winery in
                    MapAnnotation(coordinate: CLLocationCoordinate2D(
                        latitude: winery.latitude,
                        longitude: winery.longitude
                    )) {
                        WineryMapMarker(winery: winery) {
                            selectedWinery = winery
                        }
                    }
                }
                .ignoresSafeArea(edges: .bottom)
            }

            // Selected winery card at bottom - exactly like mockup
            if let selectedWinery = selectedWinery {
                VStack {
                    Spacer()
                    WineryCard(
                        winery: selectedWinery,
                        onRelatedPostsClick: {
                            // Handle related posts navigation
                        },
                        onClose: {
                            self.selectedWinery = nil
                        }
                    )
                }
            }
        }
        .onAppear {
            loadWineries()
        }
    }

    private func loadWineries() {
        // Will be loaded from PostViewModel in future
        wineries = []
    }
}

// Custom Map Marker - Red marker like in Android/mockup
struct WineryMapMarker: View {
    let winery: Winery
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            ZStack {
                // Red marker background
                Circle()
                    .fill(Color.red)
                    .frame(width: 30, height: 30)

                // Wine glass icon
                Text("🍷")
                    .font(.system(size: 16))
            }
            .shadow(radius: 3)
        }
    }
}

// Winery Card Component - exactly like Android
struct WineryCard: View {
    let winery: Winery
    let onRelatedPostsClick: () -> Void
    let onClose: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Header with heart and close
            HStack {
                Button(action: {
                    // Handle favorite toggle
                }) {
                    Image(systemName: "heart")
                        .font(.system(size: 20))
                        .foregroundColor(.gray)
                }

                VStack {
                    Text(winery.name)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.black)

                    Text(winery.location)
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                }

                Button(action: onClose) {
                    Image(systemName: "xmark")
                        .font(.system(size: 16))
                        .foregroundColor(.gray)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 16)

            // Related posts section
            VStack(spacing: 12) {
                Text("Related posts")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.black)

                Button("Related posts") {
                    onRelatedPostsClick()
                }
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(.white)
                .frame(width: 120, height: 36)
                .background(Color(red: 0.55, green: 0.44, blue: 0.28)) // 0xFF8B6F47
                .cornerRadius(18)
            }
            .padding(16)
        }
        .background(Color.white)
        .cornerRadius(16, corners: [.topLeft, .topRight])
        .shadow(radius: 8)
        .padding(.horizontal, 16)
        .animation(.spring(), value: winery.id)
    }
}

// Winery Data Model
struct Winery: Identifiable {
    let id: String
    let name: String
    let location: String
    let latitude: Double
    let longitude: Double
    let rating: Float
    let imageUrl: String
}

// Shared Header Component
struct WinemapHeader: View {
    var body: some View {
        HStack {
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
        }
        .padding(.vertical, 16)
        .background(Color(red: 0.91, green: 0.86, blue: 0.78)) // 0xFFE8DCC6
    }
}

// Helper extension for custom corner radius
extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}

// PostViewModel placeholder
class PostViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var posts: [Post] = []
}

struct Post: Identifiable {
    let id: String
    let content: String
    let rating: Int
    let userId: String
    let wineryId: String
}