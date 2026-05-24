const path = require('path');
const {getDefaultConfig} = require('expo/metro-config');

const projectRoot = __dirname;
const workspaceRoot = path.resolve(projectRoot, '../..');
const glanceKitReactNativeRoot = path.resolve(
  workspaceRoot,
  'packages/react-native',
);

const config = getDefaultConfig(projectRoot);

config.watchFolders = [workspaceRoot, glanceKitReactNativeRoot];
config.resolver.unstable_enableSymlinks = true;
config.resolver.nodeModulesPaths = [
  path.resolve(projectRoot, 'node_modules'),
  path.resolve(workspaceRoot, 'node_modules'),
];
config.resolver.extraNodeModules = {
  'glancekit': glanceKitReactNativeRoot,
};

module.exports = config;
