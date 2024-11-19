// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from config.djinni

#import <Foundation/Foundation.h>

@interface STCoverageTriggerConfig : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithReportCollectionServiceIntervalMs:(int64_t)reportCollectionServiceIntervalMs
                                    reportSubmitServiceIntervalMs:(int64_t)reportSubmitServiceIntervalMs
                                   reportSubmitServiceKeepAliveMs:(int64_t)reportSubmitServiceKeepAliveMs
                               reportCollectionServiceKeepAliveMs:(int64_t)reportCollectionServiceKeepAliveMs
                                             maxSampleFrequencyMs:(int64_t)maxSampleFrequencyMs
                                                 maxLocationAgeMs:(int64_t)maxLocationAgeMs
                                                minSampleDistance:(int64_t)minSampleDistance
                                         locationRequestTimeoutMs:(int64_t)locationRequestTimeoutMs
                                          locationRequestPriority:(int64_t)locationRequestPriority
                                                   maxSampleAgeMs:(int64_t)maxSampleAgeMs
                                            maxLocationAgeTimerMs:(int64_t)maxLocationAgeTimerMs
                                                  enabledTriggers:(nonnull NSArray<NSString *> *)enabledTriggers NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)CoverageTriggerConfigWithReportCollectionServiceIntervalMs:(int64_t)reportCollectionServiceIntervalMs
                                                     reportSubmitServiceIntervalMs:(int64_t)reportSubmitServiceIntervalMs
                                                    reportSubmitServiceKeepAliveMs:(int64_t)reportSubmitServiceKeepAliveMs
                                                reportCollectionServiceKeepAliveMs:(int64_t)reportCollectionServiceKeepAliveMs
                                                              maxSampleFrequencyMs:(int64_t)maxSampleFrequencyMs
                                                                  maxLocationAgeMs:(int64_t)maxLocationAgeMs
                                                                 minSampleDistance:(int64_t)minSampleDistance
                                                          locationRequestTimeoutMs:(int64_t)locationRequestTimeoutMs
                                                           locationRequestPriority:(int64_t)locationRequestPriority
                                                                    maxSampleAgeMs:(int64_t)maxSampleAgeMs
                                                             maxLocationAgeTimerMs:(int64_t)maxLocationAgeTimerMs
                                                                   enabledTriggers:(nonnull NSArray<NSString *> *)enabledTriggers;

@property (nonatomic, readonly) int64_t reportCollectionServiceIntervalMs;

@property (nonatomic, readonly) int64_t reportSubmitServiceIntervalMs;

@property (nonatomic, readonly) int64_t reportSubmitServiceKeepAliveMs;

@property (nonatomic, readonly) int64_t reportCollectionServiceKeepAliveMs;

@property (nonatomic, readonly) int64_t maxSampleFrequencyMs;

@property (nonatomic, readonly) int64_t maxLocationAgeMs;

@property (nonatomic, readonly) int64_t minSampleDistance;

@property (nonatomic, readonly) int64_t locationRequestTimeoutMs;

@property (nonatomic, readonly) int64_t locationRequestPriority;

@property (nonatomic, readonly) int64_t maxSampleAgeMs;

@property (nonatomic, readonly) int64_t maxLocationAgeTimerMs;

@property (nonatomic, readonly, nonnull) NSArray<NSString *> * enabledTriggers;

@end