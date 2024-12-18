// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from sdk.djinni

#import "STTaskStatus.h"
#import <Foundation/Foundation.h>


/** Can be used control the lifecycle of a test */
@interface STTaskManager : NSObject

/** Start the test run. Returns true if started, false if it is already running. */
- (nonnull STTaskStatus *)start;

/** Returns true if this task is already loaded and running  */
- (BOOL)isStarted;

- (void)cancel;

/** Supplemental Data contains extra json info that will become a part of the result json with the key "supplementalData". */
- (BOOL)setSupplementalData:(nonnull NSData *)data;

/** At the end of each stage, invoke this method to start the next stage */
- (void)startNextStage;

/** Notifies the manager the device is about to be backgrounded. Currently stops ongoing video tests */
- (void)applicationWillBeBackgrounded;

@end
