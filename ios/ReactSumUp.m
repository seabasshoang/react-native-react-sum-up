//
//  SMUPBridge.m
//  SumUpBridge
//
//  Created by Romano Schneider on 02.10.19.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "React/RCTBridgeModule.h"
//Reference to SwiftClass
@interface RCT_EXTERN_MODULE(SumUpBridge, NSObject)

RCT_EXTERN_METHOD(
  setupAPIKey:(NSString *) apikey
  resolve: (RCTPromiseResolveBlock) resolve
  rejecter: (RCTPromiseRejectBlock) reject
)

RCT_EXTERN_METHOD(
  presentLoginFromViewController: (RCTPromiseResolveBlock) resolve
  rejecter: (RCTPromiseRejectBlock) reject
)

RCT_EXTERN_METHOD(
  logout: (RCTPromiseResolveBlock) resolve
  rejecter: (RCTPromiseRejectBlock) reject
)

RCT_EXTERN_METHOD(
  loginToSumUpWithToken:(NSString *) token
  resolve:(RCTPromiseResolveBlock) resolve
  reject: (RCTPromiseRejectBlock) reject
)

RCT_EXTERN_METHOD(isLoggedIn:(RCTPromiseResolveBlock) resolve
reject: (RCTPromiseRejectBlock) reject)

RCT_EXTERN_METHOD(
  preparePaymentCheckout: (RCTPromiseResolveBlock) resolve
  reject: (RCTPromiseRejectBlock) reject
)

RCT_EXTERN_METHOD(paymentCheckout:(NSDictionary *) request
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
                  )

//when you want change the class name
//@interface RCT_EXTERN_REMAP_MODULE(RNsumupBridge, sumupBridge, NSObject)
@end
