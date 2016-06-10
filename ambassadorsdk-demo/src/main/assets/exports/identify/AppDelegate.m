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
                                 @"company" : @"{{COMPANY}}",
                                 @"phone" : @"{{PHONE}}",
                                 @"address" : @{
                                     @"street" : @"{{STREET}}"
                                     @"city" : @"{{CITY}}"
                                     @"state" : @"{{STATE}}"
                                     @"postalCode" : @"{{POSTALCODE}}"
                                     @"country" : @"{{COUNTRY}}"}
                                 };

    // Create dictionary with option to auto-enroll user in campaign
    NSDictionary *optionsDict = @{@"campaign" : @"{{CAMPAIGN}}"};

    [AmbassadorSDK identifyWithUserID:@"{{USERID}}" traits:traitsDict options:optionsDict];

    return YES;
}

@end