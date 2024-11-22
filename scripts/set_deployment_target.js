module.exports = function (ctx) {
    console.log('Running set_deployment_target.js');

    const fs = require('fs');
    const path = require('path');

    const projectRoot = ctx.opts.projectRoot;
    const platformDir = path.join(projectRoot, 'platforms', 'ios');
    const projectFilePath = path.join(platformDir, 'project.pbxproj');

    if (fs.existsSync(projectFilePath)) {
        let projectFileContent = fs.readFileSync(projectFilePath, 'utf8');
        
        // Update IPHONEOS_DEPLOYMENT_TARGET to 15.0
        projectFileContent = projectFileContent.replace(
            /IPHONEOS_DEPLOYMENT_TARGET = [0-9.]+;/g,
            'IPHONEOS_DEPLOYMENT_TARGET = 15.0;'
        );

        fs.writeFileSync(projectFilePath, projectFileContent, 'utf8');
        console.log('Deployment target updated to iOS 15.0');
    } else {
        console.log('iOS project file not found.');
    }
};
