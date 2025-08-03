import KeyboardKit
import SwiftUI

class KeyboardViewController: KeyboardInputViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set up the keyboard with the app we created above
        setup(for: .keyboardKitDemo) { _ in }
    }
    
    override func viewWillSetupKeyboardView() {
       setupKeyboardView { [weak self] controller in // <-- Use weak or unknowned if you must use self!
           KeyboardView(
              state: controller.state,
              services: controller.services,
              buttonContent: { $0.view },
              buttonView: { $0.view },
              collapsedView: { params in params.view },
              emojiKeyboard: { $0.view },
              toolbar: { $0.view }
          )
       }
    }
    
}
