//
//  CustomActionHandler.swift
//  ios-keyboard
//
//  Created by Chhunly on 3/8/25.
//

import KeyboardKit
import UIKit

class CustomActionHandler: KeyboardAction.StandardActionHandler {

    open override func handle(
        _ gesture: Keyboard.Gesture,
        on action: KeyboardAction
    ) {
        if gesture == .press && action == .keyboardType(.emojis) {
            print("Switching to emoji keyboard!")
            
        } else {
            super.handle(gesture, on: action)
        }
    }
    
    func switchEmoji() {
        print("switching")
    }
}
