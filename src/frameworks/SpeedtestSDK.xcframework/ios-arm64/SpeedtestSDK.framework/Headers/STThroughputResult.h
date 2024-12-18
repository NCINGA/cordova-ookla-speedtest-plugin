// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from model.djinni

#import "STLatencyResult.h"
#import "STPacketlossResult.h"
#import "STTransferResult.h"
#import <Foundation/Foundation.h>

/** Final result which contains the values from all the stages */
@interface STThroughputResult : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithLatency:(nullable STLatencyResult *)latency
                               download:(nullable STTransferResult *)download
                                 upload:(nullable STTransferResult *)upload
                             packetloss:(nullable STPacketlossResult *)packetloss NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)ThroughputResultWithLatency:(nullable STLatencyResult *)latency
                                           download:(nullable STTransferResult *)download
                                             upload:(nullable STTransferResult *)upload
                                         packetloss:(nullable STPacketlossResult *)packetloss;

@property (nonatomic, readonly, nullable) STLatencyResult * latency;

@property (nonatomic, readonly, nullable) STTransferResult * download;

@property (nonatomic, readonly, nullable) STTransferResult * upload;

@property (nonatomic, readonly, nullable) STPacketlossResult * packetloss;

@end
