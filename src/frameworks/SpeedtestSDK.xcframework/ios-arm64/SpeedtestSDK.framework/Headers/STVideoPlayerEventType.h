// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from video.djinni

#import <Foundation/Foundation.h>

/** Type of player event. */
typedef NS_ENUM(NSInteger, STVideoPlayerEventType)
{
    /** The player started or resumed playback. */
    STVideoPlayerEventTypePlay = 0,
    /** The player stalled playback. */
    STVideoPlayerEventTypeStall = 1,
    /** The player started to download content prior to rendering the first frame. */
    STVideoPlayerEventTypePreplayBuffer = 2,
    /** Called when the first frame is rendered. */
    STVideoPlayerEventTypeFirstFrame = 3,
    /** The rendition of the video has changed. */
    STVideoPlayerEventTypeRenditionChange = 4,
    /** Playback of the video has completed successfully. */
    STVideoPlayerEventTypeComplete = 5,
    /** The player media is on a given position. */
    STVideoPlayerEventTypeOnTime = 6,
    /** An error occurred while playing the video */
    STVideoPlayerEventTypeError = 7,
    /** iOS only. An AVPlayer log entry was generated. */
    STVideoPlayerEventTypeAccessLog = 8,
    /** The video test was canceled. */
    STVideoPlayerEventTypeCancel = 9,
};
