// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from config.djinni

#import <Foundation/Foundation.h>

@interface STLocalJobStatus : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithIsPaused:(nullable NSNumber *)isPaused
                             lastRunTime:(nullable NSNumber *)lastRunTime
                        supplementalData:(nullable NSString *)supplementalData NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)LocalJobStatusWithIsPaused:(nullable NSNumber *)isPaused
                                       lastRunTime:(nullable NSNumber *)lastRunTime
                                  supplementalData:(nullable NSString *)supplementalData;

@property (nonatomic, readonly, nullable) NSNumber * isPaused;

/** seconds since epoch (Jan 1, 1970 00:00:00) */
@property (nonatomic, readonly, nullable) NSNumber * lastRunTime;

/** Supplemental data. */
@property (nonatomic, readonly, nullable) NSString * supplementalData;

@end
