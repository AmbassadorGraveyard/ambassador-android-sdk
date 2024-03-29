#import "AppDelegate.h"
#import <Ambassador/Ambassador.h>

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [AmbassadorSDK runWithUniversalToken:@"{{SDKTOKEN}}" universalID:@"{{UNIVERSALID}}"];

    // Create dictionary for user traits
    NSDictionary *traitsDict = @{@"email" : @"{{EMAIL}}",
                                 @"firstName" : @"{{FIRSTNAME}}",
                                 @"lastName" : @"{{LASTNAME}}",
                                 @"addToGroups" : @"{{GROUPS}}",
                                 @"customLabel1" : @"{{CUSTOM1}}",
                                 @"customLabel2" : @"{{CUSTOM2}}",
                                 @"customLabel3" : @"{{CUSTOM3}}"
                                 };

    // Create dictionary with option to auto-enroll user in campaign
    NSDictionary *identifyOptionsDictionary = @{@"campaign" : @"{{CAMPAIGN}}"};

    [AmbassadorSDK identifyWithUserID:@"{{USERID}}" traits:traitsDict options:identifyOptionsDictionary];



    // Create dictionary for conversion properties
    NSDictionary *propertiesDictionary = @{@"email" : @"{{EMAIL}}",
                                 @"campaign" : @"{{CAMPAIGN}}",
                                 @"revenue" : @{{REVENUE}},
                                 @"commissionApproved" : @{{COMMISSIONAPPROVED}},
                                 @"emailNewAmbassador" : @{{EMAILNEWAMBASSADOR}},
                                 @"orderId" : @"{{ORDERID}}",
                                 @"eventData1" : @"{{EVENTDATA1}}",
                                 @"eventData2" : @"{{EVENTDATA2}}",
                                 @"eventData3" : @"{{EVENTDATA3}}"
                                 };

    // Create options dictionary for conversion
    NSDictionary *optionsDictionary = @{@"conversion" : @YES};

    [AmbassadorSDK trackEvent:@"Event Name" properties:propertiesDictionary options:optionsDictionary completion:^(AMBConversionParameters *conversion, ConversionStatus conversionStatus, NSError *error) {
        switch (conversionStatus) {
            case ConversionSuccessful:
                NSLog(@"Success!");
                break;
            case ConversionPending:
                NSLog(@"Pending!");
                break;
            case ConversionError:
                NSLog(@"Error!");
                break;
            default:
                break;
        }
    }];


    return YES;
}

@end