import UIKit

@UIApplicationMainclass AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        AmbassadorSDK.runWithUniversalToken("{{UNIVERSALTOKEN}}", universalID: "{{UNIVERSALID}}")

        // Create dictionary for user traits
        var traitsDict = ["email" : "{{EMAIL}}",
                                 "firstName" : "{{FIRSTNAME}}",
                                 "lastName" : "{{LASTNAME}}",
                                 "company" : "{{COMPANY}}",
                                 "phone" : "{{PHONE}}",
                                 "address" : [
                                     "street" : "{{STREET}}"
                                     "city" : "{{CITY}}"
                                     "state" : "{{STATE}}"
                                     "postalCode" : "{{POSTALCODE}}"
                                     "country" : "{{COUNTRY}}"]
                                 ]

        // Create dictionary with option to auto-enroll user in campaign
        var optionsDict = ["campaign" : "{{CAMPAIGN}}"]

        AmbassadorSDK.identifyWithUserID("{{USERID}}", traits: infoDict, options: optionsDict)

        return true
    }
}