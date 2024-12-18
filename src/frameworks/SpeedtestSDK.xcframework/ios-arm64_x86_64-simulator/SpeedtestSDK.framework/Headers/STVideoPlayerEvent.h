// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from video.djinni

#import "STVideoPlayerEventType.h"
#import "STVideoTestError.h"
#import <Foundation/Foundation.h>

/** Player event. Includes the `STVideoPlayerEventType` and relevant values depending on the type. */
@interface STVideoPlayerEvent : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithType:(STVideoPlayerEventType)type
                         playerWidth:(nullable NSNumber *)playerWidth
                        playerHeight:(nullable NSNumber *)playerHeight
                    renditionBitrate:(nullable NSNumber *)renditionBitrate
                         meanBitrate:(nullable NSNumber *)meanBitrate
                            position:(nullable NSNumber *)position
                               error:(nullable STVideoTestError *)error NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)VideoPlayerEventWithType:(STVideoPlayerEventType)type
                                     playerWidth:(nullable NSNumber *)playerWidth
                                    playerHeight:(nullable NSNumber *)playerHeight
                                renditionBitrate:(nullable NSNumber *)renditionBitrate
                                     meanBitrate:(nullable NSNumber *)meanBitrate
                                        position:(nullable NSNumber *)position
                                           error:(nullable STVideoTestError *)error;

/** The type of event. */
@property (nonatomic, readonly) STVideoPlayerEventType type;

/** Player width. */
@property (nonatomic, readonly, nullable) NSNumber * playerWidth;

/** Player height. */
@property (nonatomic, readonly, nullable) NSNumber * playerHeight;

/** Bitrate of the new rendition. */
@property (nonatomic, readonly, nullable) NSNumber * renditionBitrate;

/** Mean bitrate of a completed stage. */
@property (nonatomic, readonly, nullable) NSNumber * meanBitrate;

/** Position of the video being played. */
@property (nonatomic, readonly, nullable) NSNumber * position;

/** Error when the test fails. */
@property (nonatomic, readonly, nullable) STVideoTestError * error;

@end
