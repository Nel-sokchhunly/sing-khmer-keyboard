import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack(spacing: 20) {
            Text("SingKhmer Keyboard")
                .font(.title)
            Text("Go to Settings → General → Keyboard → Add New Keyboard to enable.")
                .multilineTextAlignment(.center)
                .padding()

            // add a text field to test the keyboard
            TextField("Test the keyboard", text: .constant(""))
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
        }
    }
}

#Preview {
    ContentView()
}
