const fs = require('fs');
const path = require('path');

module.exports = function (ctx) {
    const pluginId = 'SpeedTest';
    console.log(`Execution : copy_bridging_header.js`);
    const srcDirPath = path.join(
        ctx.opts.plugin.dir || path.join(ctx.opts.projectRoot, 'plugins', pluginId),
        'src/ios/frameworks/SpeedtestSDK.xcframework/ios-arm64/SpeedtestSDK.framework/Headers/'
    );

    const destDirPath = path.join(
        ctx.opts.projectRoot,
        'platforms/ios/TestApp'
    );

    console.log(`Copying files from ${srcDirPath} to ${destDirPath}`);

    if (!fs.existsSync(srcDirPath)) {
        console.error(`Source directory not found: ${srcDirPath}`);
        return;
    }
    if (!fs.existsSync(destDirPath)) {
        fs.mkdirSync(destDirPath, { recursive: true });
    }
    try {
        const files = fs.readdirSync(srcDirPath);
        files.forEach(file => {
            const srcFile = path.join(srcDirPath, file);
            const destFile = path.join(destDirPath, file);

            if (fs.lstatSync(srcFile).isFile()) {
                fs.copyFileSync(srcFile, destFile);
                console.log(`Copied ${file} to ${destDirPath}`);
            }
        });

        console.log('All files successfully copied.');
    } catch (err) {
        console.error(`Error during file copy: ${err.message}`);
    }
};
