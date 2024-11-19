// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from model.djinni

#import <Foundation/Foundation.h>

/** The percentage loss can be calculated as packetsReceived/packetsSent * 100 */
@interface STPacketlossResult : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithPacketsSent:(int64_t)packetsSent
                            packetsReceived:(int64_t)packetsReceived NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)PacketlossResultWithPacketsSent:(int64_t)packetsSent
                                        packetsReceived:(int64_t)packetsReceived;

@property (nonatomic, readonly) int64_t packetsSent;

@property (nonatomic, readonly) int64_t packetsReceived;

@end