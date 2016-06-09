import UIKit

@UIApplicationMainclass AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        AmbassadorSDK.runWithUniversalToken("{{SDKTOKEN}}", universalID: "{{UNIVERSALID}}")

        // Create dictionary for user traits
        let traitsDict = ["email" : "{{EMAIL}}",
                                 "firstName" : "{{FIRSTNAME}}",
                                 "lastName" : "{{LASTNAME}}",
                                 "addToGroups" : "{{GROUPS}}",
                                 "customLabel1" : "{{CUSTOM1}}",
                                 "customLabel2" : "{{CUSTOM2}}",
                                 "customLabel3" : "{{CUSTOM3}}"
                                 ]

        // Create dictionary with option to auto-enroll user in campaign
        let identifyOptionsDictionary = ["campaign" : "{{CAMPAIGN}}"]

        AmbassadorSDK.identifyWithUserID("{{USERID}}", traits:traitsDict, options:identifyOptionsDictionary)

        // Create dictionary for conversion properties
        let propertiesDictionary = ["email" : "{{EMAIL}}",
                                 "campaign" : "{{CAMPAIGN}}",
                                 "revenue" : {{REVENUE}},
                                 "commissionApproved" : {{COMMISSIONAPPROVED}},
                                 "emailNewAmbassador" : {{EMAILNEWAMBASSADOR}},
                                 "orderId" : "{{ORDERID}}",
                                 "eventData1" : "{{EVENTDATA1}}",
                                 "eventData2" : "{{EVENTDATA2}}",
                                 "eventData3" : "{{EVENTDATA3}}"
                                 ]

        // Create options dictionary for conversion
        let optionsDictionary = ["conversion" : true]

        AmbassadorSDK.trackEvent("Event Name", properties:propertiesDictionary, options:optionsDictionary) { (parameters, conversionStatus, error) in
            switch conversionStatus {
            case ConversionSuccessful:
                print("Success!")
                break
            case ConversionPending:
                print("Pending!")
                break
            case ConversionError:
                print("Error!")
                break
            default:
                break
            }
        }

        return true
    }
}