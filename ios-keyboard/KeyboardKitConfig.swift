//
//  KeyboardConfigKit.swift
//  ios-keyboard
//
//  Created by Chhunly on 3/8/25.
//

import KeyboardKit
import SwiftUI

extension KeyboardApp {
    static var keyboardKitDemo: KeyboardApp {
        .init(
            name: "KeyboardKit",
            locales: .keyboardKitSupported
        )
    }
}

// custom emoji keyboard
// the usage is to show the smiley face btn 
// so we can intercept the action and show the native emoji keyboard
struct CustomEmojiKeyboard: View {
    var body: some View {
        Text("Emoji Keyboard is in development")
        Text("Please use the default emoji keyboard")
        
    }
}
