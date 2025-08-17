import SwiftUI
import Combine
import CoreLocation
import shared

@MainActor
class SearchViewModel: ObservableObject {

    // MARK: - Published Properties

    // Search State
    @Published var searchQuery = ""
    @Published var searchResults: [WinerySearchResult] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var hasSearched = false

    // Search History
    @Published var recentSearches: [String] = []
    @Published var popularSearches: [String] = []
    @Published var showSearchHistory = true

    // Filters
    @Published var selectedRegion = ""
    @Published var availableRegions: [String] = []
    @Published var showFilters = false
    @Published var minRating: Float = 0.0
    @Published var maxDistance: Double = 100.0 // km
    @Published var sortBy: SearchSortOption = .relevance
    @Published var showOnlyWithPosts = false

    // Location
    @Published var userLocation: CLLocationCoordinate2D?
    @Published var locationPermissionGranted = false
    @Published var showLocationPrompt = false

    // UI State
    @Published var isSearchFocused = false
    @Published var showNoResults = false
    @Published var selectedWinery: WinerySearchResult?

    // Advanced Search
    @Published var showAdvancedSearch = false
    @Published var searchByName = true
    @Published var searchByLocation = false
    @Published var searchByDescription = false

    // MARK: - Private Properties
    private var cancellables = Set<AnyCancellable>()
    private var searchDebounceTimer: AnyCancellable?
    private let locationManager = CLLocationManager()

    // Shared ViewModels
    private let sharedSearchViewModel: shared.SearchViewModel
    private let sharedPostViewModel: shared.PostViewModel
    private var searchStateObserver: Kotlinx_coroutines_coreDisposableHandle?
    private var postStateObserver: Kotlinx_coroutines_coreDisposableHandle?

    // Constants
    private let maxRecentSearches = 10
    private let searchHistoryKey = "SearchHistory"
    private let popularSearchesKey = "PopularSearches"

    // MARK: - Initialization
    init() {
        self.sharedSearchViewModel = ViewModelProvider.companion.searchViewModel
        self.sharedPostViewModel = ViewModelProvider.companion.postViewModel

        setupLocationManager()
        observeSharedState()
        loadSearchHistory()
        setupSearchDebouncing()
        loadInitialData()
    }

    // MARK: - Location Setup
    private func setupLocationManager() {
        locationManager.delegate = SearchLocationDelegate(searchViewModel: self)
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationPermissionGranted = locationManager.authorizationStatus == .authorizedWhenInUse ||
                                   locationManager.authorizationStatus == .authorizedAlways
    }

    // MARK: - Observe Shared State
    private func observeSharedState() {
        // Observe Search State
        searchStateObserver = sharedSearchViewModel.uiState.watch { [weak self] searchUiState in
            guard let self = self,
                  let state = searchUiState as? SearchUiState else { return }

            DispatchQueue.main.async {
                self.updateFromSearchState(state)
            }
        }
    }

    private func updateFromSearchState(_ state: SearchUiState) {
        self.isLoading = state.isLoading
        self.errorMessage = state.errorMessage
        self.availableRegions = state.availableRegions

        // Convert Kotlin WineryInfo to iOS WinerySearchResult
        let convertedResults = state.searchResults.compactMap { kotlinWinery -> WinerySearchResult? in
            guard let wineryInfo = kotlinWinery as? WineryInfo else { return nil }

            return WinerySearchResult(
                id: wineryInfo.name,
                name: wineryInfo.name,
                region: wineryInfo.region,
                location: CLLocationCoordinate2D(
                    latitude: wineryInfo.location.latitude,
                    longitude: wineryInfo.location.longitude
                ),
                description: wineryInfo.description,
                rating: 0.0, // Will be calculated from posts
                totalPosts: 0, // Will be calculated from posts
                distance: calculateDistance(to: wineryInfo.location),
                imageUrl: "",
                isOpen: true,
                tags: extractTags(from: wineryInfo.description)
            )
        }

        self.searchResults = convertedResults
        self.showNoResults = convertedResults.isEmpty && hasSearched && !state.isLoading

        // Update recent searches from shared state
        self.recentSearches = state.recentSearches

        // Apply local filters if any
        applyLocalFilters()
    }

    // MARK: - Search Functionality
    private func setupSearchDebouncing() {
        $searchQuery
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .removeDuplicates()
            .sink { [weak self] query in
                self?.performSearch(query)
            }
            .store(in: &cancellables)
    }

    func performSearch(_ query: String? = nil) {
        let searchTerm = query ?? searchQuery

        guard !searchTerm.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            clearSearch()
            return
        }

        hasSearched = true
        showSearchHistory = false

        // Add to recent searches
        addToRecentSearches(searchTerm)

        // Perform search through shared module
        sharedSearchViewModel.searchWineries(query: searchTerm)
    }

    func quickSearch(_ query: String) {
        searchQuery = query
        performSearch(query)
    }

    func clearSearch() {
        searchQuery = ""
        searchResults = []
        hasSearched = false
        showNoResults = false
        showSearchHistory = true
        selectedWinery = nil

        sharedSearchViewModel.clearSearch()
    }

    // MARK: - Search History
    private func loadSearchHistory() {
        if let data = UserDefaults.standard.data(forKey: searchHistoryKey),
           let history = try? JSONDecoder().decode([String].self, from: data) {
            recentSearches = history
        }

        if let data = UserDefaults.standard.data(forKey: popularSearchesKey),
           let popular = try? JSONDecoder().decode([String].self, from: data) {
            popularSearches = popular
        } else {
            // Default popular searches
            popularSearches = ["יקב ספרה", "יקב גליל", "יקבי הכרמל", "יקב ברקן", "יקב מרגליות"]
        }
    }

    private func addToRecentSearches(_ query: String) {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty, !recentSearches.contains(trimmed) else { return }

        recentSearches.insert(trimmed, at: 0)
        if recentSearches.count > maxRecentSearches {
            recentSearches = Array(recentSearches.prefix(maxRecentSearches))
        }

        saveSearchHistory()
    }

    private func saveSearchHistory() {
        if let data = try? JSONEncoder().encode(recentSearches) {
            UserDefaults.standard.set(data, forKey: searchHistoryKey)
        }
    }

    func clearSearchHistory() {
        recentSearches.removeAll()
        UserDefaults.standard.removeObject(forKey: searchHistoryKey)
    }

    func removeFromRecentSearches(_ query: String) {
        recentSearches.removeAll { $0 == query }
        saveSearchHistory()
    }

    // MARK: - Filters
    func applyRegionFilter(_ region: String) {
        selectedRegion = region
        if !region.isEmpty {
            sharedSearchViewModel.filterByRegion(region: region)
        } else {
            loadInitialData()
        }
    }

    func clearAllFilters() {
        selectedRegion = ""
        minRating = 0.0
        maxDistance = 100.0
        sortBy = .relevance
        showOnlyWithPosts = false

        applyLocalFilters()
    }

    private func applyLocalFilters() {
        var filteredResults = searchResults

        // Filter by rating
        if minRating > 0 {
            filteredResults = filteredResults.filter { $0.rating >= minRating }
        }

        // Filter by distance
        if let userLocation = userLocation, maxDistance < 100 {
            filteredResults = filteredResults.filter { result in
                guard let distance = result.distance else { return true }
                return distance <= maxDistance
            }
        }

        // Filter by posts existence
        if showOnlyWithPosts {
            filteredResults = filteredResults.filter { $0.totalPosts > 0 }
        }

        // Sort results
        switch sortBy {
        case .relevance:
            // Keep original order from search
            break
        case .distance:
            if userLocation != nil {
                filteredResults.sort { ($0.distance ?? Double.max) < ($1.distance ?? Double.max) }
            }
        case .rating:
            filteredResults.sort { $0.rating > $1.rating }
        case .name:
            filteredResults.sort { $0.name < $1.name }
        case .popularity:
            filteredResults.sort { $0.totalPosts > $1.totalPosts }
        }

        searchResults = filteredResults
    }

    // MARK: - Location Services
    func requestLocationPermission() {
        switch locationManager.authorizationStatus {
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        case .denied, .restricted:
            showLocationPrompt = true
        case .authorizedWhenInUse, .authorizedAlways:
            startLocationUpdates()
        @unknown default:
            break
        }
    }

    private func startLocationUpdates() {
        guard locationPermissionGranted else { return }
        locationManager.startUpdatingLocation()
    }

    func updateUserLocation(_ location: CLLocationCoordinate2D) {
        userLocation = location
        updateDistancesForResults()
    }

    func updateLocationPermission(_ granted: Bool) {
        locationPermissionGranted = granted
        if granted {
            startLocationUpdates()
        }
    }

    private func updateDistancesForResults() {
        guard let userLocation = userLocation else { return }

        searchResults = searchResults.map { result in
            var updatedResult = result
            updatedResult.distance = calculateDistance(
                from: userLocation,
                to: result.location
            )
            return updatedResult
        }

        // Re-apply filters to update sorting
        applyLocalFilters()
    }

    // MARK: - Winery Selection
    func selectWinery(_ winery: WinerySearchResult) {
        selectedWinery = winery
        loadWineryDetails(winery)
    }

    func deselectWinery() {
        selectedWinery = nil
    }

    private func loadWineryDetails(_ winery: WinerySearchResult) {
        // Load posts for this winery
        sharedPostViewModel.loadWineryPosts(wineryName: winery.name)
    }

    // MARK: - Helper Functions
    private func loadInitialData() {
        // Load all wineries initially
        sharedSearchViewModel.updateSearchQuery(query: "")
    }

    private func calculateDistance(to location: shared.Location) -> Double? {
        guard let userLocation = userLocation else { return nil }
        return calculateDistance(
            from: userLocation,
            to: CLLocationCoordinate2D(latitude: location.latitude, longitude: location.longitude)
        )
    }

    private func calculateDistance(from: CLLocationCoordinate2D, to: CLLocationCoordinate2D) -> Double {
        let fromLocation = CLLocation(latitude: from.latitude, longitude: from.longitude)
        let toLocation = CLLocation(latitude: to.latitude, longitude: to.longitude)
        return fromLocation.distance(from: toLocation) / 1000 // Convert to km
    }

    private func extractTags(from description: String) -> [String] {
        // Simple tag extraction - can be enhanced
        let keywords = ["organic", "kosher", "boutique", "family", "premium", "tours", "tasting"]
        return keywords.filter { description.lowercased().contains($0) }
    }

    // MARK: - Error Handling
    func clearError() {
        errorMessage = nil
        sharedSearchViewModel.clearSearchError()
    }

    // MARK: - Computed Properties
    var hasActiveFilters: Bool {
        return !selectedRegion.isEmpty || minRating > 0 || maxDistance < 100 ||
               showOnlyWithPosts || sortBy != .relevance
    }

    var searchPlaceholder: String {
        if !selectedRegion.isEmpty {
            return "Search in \(selectedRegion)"
        }
        return "Find a winery..."
    }

    var resultsCount: Int {
        return searchResults.count
    }

    var hasResults: Bool {
        return !searchResults.isEmpty
    }

    var shouldShowLocationButton: Bool {
        return !locationPermissionGranted
    }

    // MARK: - Cleanup
    deinit {
        searchStateObserver?.dispose()
        postStateObserver?.dispose()
        searchDebounceTimer?.cancel()
        locationManager.stopUpdatingLocation()
        sharedSearchViewModel.onCleared()
    }
}

// MARK: - Supporting Models

struct WinerySearchResult: Identifiable, Hashable {
    let id: String
    let name: String
    let region: String
    let location: CLLocationCoordinate2D
    let description: String
    var rating: Float
    var totalPosts: Int
    var distance: Double?
    let imageUrl: String
    let isOpen: Bool
    let tags: [String]

    // Hashable conformance
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }

    static func == (lhs: WinerySearchResult, rhs: WinerySearchResult) -> Bool {
        return lhs.id == rhs.id
    }

    // Computed properties
    var formattedDistance: String? {
        guard let distance = distance else { return nil }
        if distance < 1 {
            return String(format: "%.0f m", distance * 1000)
        } else {
            return String(format: "%.1f km", distance)
        }
    }

    var formattedRating: String {
        return String(format: "%.1f", rating)
    }
}

enum SearchSortOption: String, CaseIterable {
    case relevance = "Relevance"
    case distance = "Distance"
    case rating = "Rating"
    case name = "Name"
    case popularity = "Popularity"

    var icon: String {
        switch self {
        case .relevance: return "star.fill"
        case .distance: return "location.fill"
        case .rating: return "heart.fill"
        case .name: return "textformat.abc"
        case .popularity: return "flame.fill"
        }
    }
}

// MARK: - Location Manager Delegate
class SearchLocationDelegate: NSObject, CLLocationManagerDelegate {
    weak var searchViewModel: SearchViewModel?

    init(searchViewModel: SearchViewModel) {
        self.searchViewModel = searchViewModel
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }

        DispatchQueue.main.async {
            self.searchViewModel?.updateUserLocation(location.coordinate)
        }
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        DispatchQueue.main.async {
            let granted = status == .authorizedWhenInUse || status == .authorizedAlways
            self.searchViewModel?.updateLocationPermission(granted)
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        DispatchQueue.main.async {
            self.searchViewModel?.errorMessage = "Location error: \(error.localizedDescription)"
        }
    }
}

// MARK: - Preview Helper
extension SearchViewModel {
    static func preview() -> SearchViewModel {
        let viewModel = SearchViewModel()

        // Mock search results
        viewModel.searchResults = [
            WinerySearchResult(
                id: "1",
                name: "יקב ספרה",
                region: "שומרון",
                location: CLLocationCoordinate2D(latitude: 32.4279, longitude: 35.0818),
                description: "יקב בוטיק עם יינות איכותיים",
                rating: 4.5,
                totalPosts: 23,
                distance: 12.5,
                imageUrl: "",
                isOpen: true,
                tags: ["boutique", "premium"]
            ),
            WinerySearchResult(
                id: "2",
                name: "יקב גליל",
                region: "גליל עליון",
                location: CLLocationCoordinate2D(latitude: 33.0522, longitude: 35.3888),
                description: "יקב מסורתי עם סיורים",
                rating: 4.2,
                totalPosts: 45,
                distance: 28.3,
                imageUrl: "",
                isOpen: true,
                tags: ["tours", "family"]
            )
        ]

        viewModel.recentSearches = ["יקב ספרה", "גליל", "כרמל"]
        viewModel.availableRegions = ["גליל עליון", "שומרון", "הכרמל"]

        return viewModel
    }
}