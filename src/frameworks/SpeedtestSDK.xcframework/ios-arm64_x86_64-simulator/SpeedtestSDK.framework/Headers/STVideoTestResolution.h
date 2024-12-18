// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from video.djinni

#import <Foundation/Foundation.h>

/** Resolution of a Rendition */
@interface STVideoTestResolution : NSObject
- (nonnull instancetype)init NS_UNAVAILABLE;
+ (nonnull instancetype)new NS_UNAVAILABLE;
- (nonnull instancetype)initWithWidthPx:(int32_t)widthPx
                               heightPx:(int32_t)heightPx NS_DESIGNATED_INITIALIZER;
+ (nonnull instancetype)VideoTestResolutionWithWidthPx:(int32_t)widthPx
                                              heightPx:(int32_t)heightPx;

@property (nonatomic, readonly) int32_t widthPx;

@property (nonatomic, readonly) int32_t heightPx;

@end
