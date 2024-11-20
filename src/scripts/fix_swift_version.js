// fix_swift_version.js
module.exports = function (ctx) {
    const fs = require('fs');
    const path = require('path');

    const projectRoot = ctx.opts.projectRoot;
    const platformDir = path.join(projectRoot, 'platforms', 'ios');
    const configPath = path.join(platformDir, 'cordova', 'build.xcconfig');

    if (fs.existsSync(configPath)) {
        fs.appendFileSync(configPath, '\nUSE_SWIFT_LANGUAGE_VERSION=5.0\n');
        console.log('USE_SWIFT_LANGUAGE_VERSION set to 5.0 in build.xcconfig');
    }
};
