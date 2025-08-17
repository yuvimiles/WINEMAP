import SwiftUI

struct MainView: View {
    @State private var selectedTab: TabItem = .feed

    var body: some View {
        VStack(spacing: 0) {
            // Main content area
            Group {
                switch selectedTab {
                case .feed:
                    MapView()
                case .favorites:
                    FavoritesView()
                case .add:
                    CreatePostView()
                case .search:
                    SearchView()
                case .profile:
                    ProfileView()
                }
            }

            // Custom Bottom Navigation
            BottomNavigationBar(
                selectedTab: $selectedTab
            )
        }
        .ignoresSafeArea(.keyboard)
    }
}

// Custom Bottom Navigation Bar - Like Android
struct BottomNavigationBar: View {
    @Binding var selectedTab: TabItem

    var body: some View {
        HStack {
            ForEach(TabItem.allCases, id: \.self) { tab in
                if tab == .add {
                    // Center FAB button
                    Button(action: {
                        selectedTab = tab
                    }) {
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.55, green: 0.44, blue: 0.28)) // 0xFF8B6F47
                                .frame(width: 56, height: 56)

                            Text("➕")
                                .font(.system(size: 24))
                                .foregroundColor(.white)
                        }
                    }
                    .offset(y: -10) // Elevated FAB
                } else {
                    // Regular tab buttons
                    Button(action: {
                        selectedTab = tab
                    }) {
                        VStack(spacing: 4) {
                            Text(tab.icon)
                                .font(.system(size: 24))
                                .foregroundColor(selectedTab == tab ?
                                    Color(red: 0.42, green: 0.36, blue: 0.45) : // 0xFF6B5B73
                                    Color.gray
                                )

                            Text(tab.title)
                                .font(.system(size: 10))
                                .foregroundColor(selectedTab == tab ?
                                    Color(red: 0.42, green: 0.36, blue: 0.45) :
                                    Color.gray
                                )
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
            }
        }
        .frame(height: 80)
        .background(Color(red: 0.91, green: 0.86, blue: 0.78)) // 0xFFE8DCC6
        .ignoresSafeArea(.keyboard)
    }
}

// Tab Items Enum
enum TabItem: CaseIterable {
    case feed
    case favorites
    case add
    case search
    case profile

    var title: String {
        switch self {
        case .feed: return "Feed"
        case .favorites: return "Favorites"
        case .add: return "Add"
        case .search: return "Search"
        case .profile: return "Profile"
        }
    }

    var icon: String {
        switch self {
        case .feed: return "🏠"
        case .favorites: return "❤️"
        case .add: return "➕"
        case .search: return "🔍"
        case .profile: return "👤"
        }
    }
}

// Placeholder Views - will be implemented properly later
struct FavoritesView: View {
    var body: some View {
        VStack {
            WinemapHeader()

            Text("My favorite")
                .font(.title2)
                .fontWeight(.bold)
                .padding()

            Spacer()

            Text("❤️ Your favorite wineries")
                .font(.title3)
                .foregroundColor(.gray)

            Spacer()
        }
        .background(Color(.systemGroupedBackground))
    }
}

struct CreatePostView: View {
    var body: some View {
        VStack {
            WinemapHeader()

            Text("Create new post")
                .font(.title2)
                .fontWeight(.bold)
                .padding()

            Spacer()

            Text("📝 Create a new post")
                .font(.title3)
                .foregroundColor(.gray)

            Spacer()
        }
        .background(Color(.systemGroupedBackground))
    }
}

struct SearchView: View {
    var body: some View {
        VStack {
            WinemapHeader()

            Spacer()

            Text("🔍 Search wineries")
                .font(.title3)
                .foregroundColor(.gray)

            Spacer()
        }
        .background(Color(.systemGroupedBackground))
    }
}

struct ProfileView: View {
    var body: some View {
        VStack {
            WinemapHeader()

            Spacer()

            Text("👤 Your profile")
                .font(.title3)
                .foregroundColor(.gray)

            Spacer()
        }
        .background(Color(.systemGroupedBackground))
    }
}
