import SwiftUI

struct SplashView: View {
    @State private var scale: CGFloat = 0.0

    let onNavigateToAuth: () -> Void

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Background Image
                Image("winemap_bg")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: geometry.size.width, height: geometry.size.height)
                    .clipped()
                    .opacity(0.9)

                // White Circle with Content
                Circle()
                    .fill(Color.white.opacity(0.95))
                    .frame(width: 350, height: 350)
                    .overlay(
                        VStack(spacing: 0) {
                            // Wine Logo
                            Image("winemap_logo")
                                .resizable()
                                .frame(width: 100, height: 100)
                                .offset(y: -30)

                            // Description Text
                            Text("The easy way to\ndiscover, rate and\nshare experiences\nfrom all wineries in\nIsrael")
                                .font(.system(size: 16))
                                .foregroundColor(.black)
                                .multilineTextAlignment(.center)
                                .lineSpacing(4)
                                .padding(.top, 8)

                            Spacer().frame(height: 16)

                            // LET'S GO Button
                            Button(action: onNavigateToAuth) {
                                Text("LET'S GO !")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(.white)
                                    .frame(width: 120, height: 40)
                                    .background(Color(red: 0.42, green: 0.36, blue: 0.45))
                                    .cornerRadius(20)
                            }
                        }
                        .padding(40)
                    )
                    .scaleEffect(scale)
                    .animation(.spring(response: 0.8, dampingFraction: 0.6, blendDuration: 0), value: scale)
            }
        }
        .ignoresSafeArea()
        .onAppear {
            withAnimation {
                scale = 1.0
            }
        }
    }
}