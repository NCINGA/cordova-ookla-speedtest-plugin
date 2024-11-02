// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from video.djinni

#import <Foundation/Foundation.h>

/** Type of error encountered during a stage. */
typedef NS_ENUM(NSInteger, STVideoStageErrorType)
{
    /** An error happened during playback. */
    STVideoStageErrorTypePlayerError = 0,
    /** The user canceled the test. */
    STVideoStageErrorTypeUserCancel = 1,
    /** The user backgrounded the application. */
    STVideoStageErrorTypeUserBackground = 2,
    /** Stage timed out before the first frame was rendered. */
    STVideoStageErrorTypeStartTimeout = 3,
    /** Stage timed out after the first frame was rendered. */
    STVideoStageErrorTypeTimeout = 4,
    /** Other error. */
    STVideoStageErrorTypeUnknown = 5,
};