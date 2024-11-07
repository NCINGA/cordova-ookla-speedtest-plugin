var exec = require('cordova/exec');

exports.startTesting = function (arg0, success, error) {
    exec(success, error, 'SpeedTest', 'startTesting', [arg0]);
};
exports.stopTesting = function (arg0, success, error) {
    exec(success, error, 'SpeedTest', 'stopTesting', [arg0]);
};
