import SwiftUI
import Combine
import MapKit
import CoreLocation
import shared

@MainActor
class MapViewModel: ObservableObject {

    // MARK: - Published Properties
    @Published var wineries: [Winery] = []
    @Published var selectedWinery: Winery?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var userLocation: CLLocationCoordinate2D?
    @Published var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 32.7767, longitude: 35.0231), // Israel center
        span: MKCoordinateSpan(latitudeDelta: 2.0, longitudeDelta: 2.0)
    )

    // Location permissions
    @Published var locationPermissionStatus: CLAuthorizationStatus = .notDetermined
    @Published var showLocationAlert = false

    // Filter options
    @Published var selectedRegion: String = ""
    @Published var availableRegions: [String] = []
    @Published var showNearbyOnly = false
    @Published var nearbyRadius: Double = 50.0 // km

    // MARK: - Private Properties
    private var cancellables = Set<AnyCancellable>()
    private let locationManager = CLLocationManager()

    // Shared ViewModels
    private let sharedSearchViewModel: shared.SearchViewModel
    private let sharedPostViewModel: shared.PostViewModel
    private var searchStateObserver: Kotlinx_coroutines_coreDisposableHandle?
    private var postStateObserver: Kotlinx_coroutines_coreDisposableHandle?

    // MARK: - Initialization
    init() {
        self.sharedSearchViewModel = ViewModelProvider.companion.searchViewModel
        self.sharedPostViewModel = ViewModelProvider.companion.postViewModel

        setupLocationManager()
        observeSharedState()
        loadWineries()
    }

    // MARK: - Location Manager Setup
    private func setupLocationManager() {
        locationManager.delegate = LocationManagerDelegate(mapViewModel: self)
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationPermissionStatus = locationManager.authorizationStatus
    }

    // MARK: - Observe Shared State
    private func observeSharedState() {
        // Observe search results for wineries
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

        // Convert Kotlin WineryInfo to iOS Winery
        self.wineries = state.searchResults.compactMap { kotlinWinery in
            guard let wineryInfo = kotlinWinery as? WineryInfo else { return nil }
            return Winery(
                id: wineryInfo.name,
                name: wineryInfo.name,
                location: wineryInfo.region,
                latitude: wineryInfo.location.latitude,
                longitude: wineryInfo.location.longitude,
                rating: 4.0, // Default - will be calculated from posts
                imageUrl: "",
                description: wineryInfo.description
            )
        }
    }

    // MARK: - Load Wineries
    func loadWineries() {
        // Load all wineries through search
        sharedSearchViewModel.updateSearchQuery(query: "")
    }

    func loadWineriesInRegion(_ region: String) {
        selectedRegion = region
        sharedSearchViewModel.filterByRegion(region: region)
    }

    func searchWineries(_ query: String) {
        sharedSearchViewModel.searchWineries(query: query)
    }

    // MARK: - Winery Selection
    func selectWinery(_ winery: Winery) {
        selectedWinery = winery

        // Center map on selected winery
        withAnimation(.easeInOut(duration: 0.5)) {
            region = MKCoordinateRegion(
                center: CLLocationCoordinate2D(
                    latitude: winery.latitude,
                    longitude: winery.longitude
                ),
                span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1)
            )
        }

        // Load posts for this winery
        loadWineryPosts(winery.name)
    }

    func deselectWinery() {
        selectedWinery = nil

        // Return to Israel view
        withAnimation(.easeInOut(duration: 0.5)) {
            region = MKCoordinateRegion(
                center: CLLocationCoordinate2D(latitude: 32.7767, longitude: 35.0231),
                span: MKCoordinateSpan(latitudeDelta: 2.0, longitudeDelta: 2.0)
            )
        }
    }

    private func loadWineryPosts(_ wineryName: String) {
        sharedPostViewModel.loadWineryPosts(wineryName: wineryName)
    }

    // MARK: - Location Services
    func requestLocationPermission() {
        switch locationManager.authorizationStatus {
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        case .denied, .restricted:
            showLocationAlert = true
        case .authorizedWhenInUse, .authorizedAlways:
            startLocationUpdates()
        @unknown default:
            break
        }
    }

    private func startLocationUpdates() {
        guard locationManager.authorizationStatus == .authorizedWhenInUse ||
              locationManager.authorizationStatus == .authorizedAlways else { return }

        locationManager.startUpdatingLocation()
    }

    func stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
    }

    // Called by LocationManagerDelegate
    func updateUserLocation(_ location: CLLocationCoordinate2D) {
        userLocation = location

        if showNearbyOnly {
            filterNearbyWineries()
        }
    }

    func updateLocationPermissionStatus(_ status: CLAuthorizationStatus) {
        locationPermissionStatus = status

        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            startLocationUpdates()
        case .denied, .restricted:
            stopLocationUpdates()
        default:
            break
        }
    }

    // MARK: - Nearby Wineries
    func toggleNearbyFilter() {
        showNearbyOnly.toggle()

        if showNearbyOnly {
            requestLocationPermission()
            filterNearbyWineries()
        } else {
            loadWineries()
        }
    }

    private func filterNearbyWineries() {
        guard let userLocation = userLocation else {
            loadWineries()
            return
        }

        // Filter wineries within radius
        let userCLLocation = CLLocation(
            latitude: userLocation.latitude,
            longitude: userLocation.longitude
        )

        let nearbyWineries = wineries.filter { winery in
            let wineryLocation = CLLocation(
                latitude: winery.latitude,
                longitude: winery.longitude
            )
            let distance = userCLLocation.distance(from: wineryLocation) / 1000 // Convert to km
            return distance <= nearbyRadius
        }

        self.wineries = nearbyWineries
    }

    func updateNearbyRadius(_ radius: Double) {
        nearbyRadius = radius
        if showNearbyOnly {
            filterNearbyWineries()
        }
    }

    // MARK: - Map Actions
    func centerOnUserLocation() {
        guard let userLocation = userLocation else {
            requestLocationPermission()
            return
        }

        withAnimation(.easeInOut(duration: 0.5)) {
            region = MKCoordinateRegion(
                center: userLocation,
                span: MKCoordinateSpan(latitudeDelta: 0.5, longitudeDelta: 0.5)
            )
        }
    }

    func centerOnIsrael() {
        withAnimation(.easeInOut(duration: 0.5)) {
            region = MKCoordinateRegion(
                center: CLLocationCoordinate2D(latitude: 32.7767, longitude: 35.0231),
                span: MKCoordinateSpan(latitudeDelta: 2.0, longitudeDelta: 2.0)
            )
        }
    }

    // MARK: - Favorites
    func toggleFavorite(for winery: Winery) {
        // This will be implemented when we have user favorites in shared module
        // For now, just show feedback
        print("Toggle favorite for: \(winery.name)")
    }

    func isFavorite(_ winery: Winery) -> Bool {
        // This will be implemented when we have user favorites in shared module
        return false
    }

    // MARK: - Error Handling
    func clearError() {
        errorMessage = nil
        sharedSearchViewModel.clearSearchError()
    }

    // MARK: - Computed Properties
    var nearbyWineriesCount: Int {
        guard showNearbyOnly, let userLocation = userLocation else { return wineries.count }

        let userCLLocation = CLLocation(
            latitude: userLocation.latitude,
            longitude: userLocation.longitude
        )

        return wineries.filter { winery in
            let wineryLocation = CLLocation(
                latitude: winery.latitude,
                longitude: winery.longitude
            )
            let distance = userCLLocation.distance(from: wineryLocation) / 1000
            return distance <= nearbyRadius
        }.count
    }

    var hasLocationPermission: Bool {
        return locationPermissionStatus == .authorizedWhenInUse ||
               locationPermissionStatus == .authorizedAlways
    }

    // MARK: - Cleanup
    deinit {
        searchStateObserver?.dispose()
        postStateObserver?.dispose()
        stopLocationUpdates()
        sharedSearchViewModel.onCleared()
    }
}

// MARK: - Location Manager Delegate
class LocationManagerDelegate: NSObject, CLLocationManagerDelegate {
    weak var mapViewModel: MapViewModel?

    init(mapViewModel: MapViewModel) {
        self.mapViewModel = mapViewModel
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }

        DispatchQueue.main.async {
            self.mapViewModel?.updateUserLocation(location.coordinate)
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        DispatchQueue.main.async {
            self.mapViewModel?.errorMessage = "Location error: \(error.localizedDescription)"
        }
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        DispatchQueue.main.async {
            self.mapViewModel?.updateLocationPermissionStatus(status)
        }
    }
}

// MARK: - Winery Model Extension
extension Winery {
    // Distance from user location
    func distance(from userLocation: CLLocationCoordinate2D) -> Double {
        let userCLLocation = CLLocation(
            latitude: userLocation.latitude,
            longitude: userLocation.longitude
        )
        let wineryLocation = CLLocation(
            latitude: self.latitude,
            longitude: self.longitude
        )
        return userCLLocation.distance(from: wineryLocation) / 1000 // km
    }

    // Formatted distance string
    func formattedDistance(from userLocation: CLLocationCoordinate2D) -> String {
        let dist = distance(from: userLocation)
        if dist < 1 {
            return String(format: "%.0f m", dist * 1000)
        } else {
            return String(format: "%.1f km", dist)
        }
    }
}

// MARK: - Updated Winery Model
struct Winery: Identifiable, Equatable {
    let id: String
    let name: String
    let location: String
    let latitude: Double
    let longitude: Double
    let rating: Float
    let imageUrl: String
    let description: String

    init(id: String, name: String, location: String, latitude: Double, longitude: Double, rating: Float, imageUrl: String, description: String = "") {
        self.id = id
        self.name = name
        self.location = location
        self.latitude = latitude
        self.longitude = longitude
        self.rating = rating
        self.imageUrl = imageUrl
        self.description = description
    }
}

// MARK: - Region Constants
extension MapViewModel {
    static let israelRegions = [
        "גליל עליון",
        "גליל תחתון",
        "הכרמל",
        "שפלת יהודה",
        "הרי יהודה",
        "בקעת הירדן",
        "הנגב",
        "רמת הגולן",
        "שומרון",
        "עמק חפר"
    ]
}