# broachcutter-vendor-app
Native Android app for Broachcutter vendors

![Android Pull Request](https://github.com/valartech/broachcutter-vendor-app/workflows/Android%20Pull%20Request/badge.svg)

## Keystore.properties
Copy the `keystore.properties.sample` in the root of the project after cloning and save the copy as
`keystore.properties`. Set the appropriate values in this file to make release builds.

## Spotless pre-commit hook
If you're running Mac or Linux, uncomment the `createSpotlessPreCommitHook` in `./spotless.gradle` and
run it. This will make spotless fix your staged files on commit. **You may need to amend your commit 
with the changes that spotless makes before you push your branch.**

Be sure to comment it out again when you're done, this task breaks the build for our Windows-using colleagues.  

## Publishing steps
- On `development` branch, update content in `releaseNotes/whatsnew-en-IN`
- Update `getAppVersionCode` and `getAppVersionName` in `app/build.gradle`.
- Push to `development`. 
- Merge `development` into `master`.
- Create and push git tag of the format `v*.*.*`
  - By default, only a github release is created.
  - Add the following to the end of the tag with hyphens to publish to different channels. 
    - `firebase` for Firebase app distribution
    - `beta` for Beta track on Play Store
    - `prod` for Production track on Play Store   
  - Ex: `v0.8.1-firebase-beta-prod` to publish to all 3, `v0.8.1-firebase` to just firebase, etc.
  - If you wish to at a later time promote a release, push another tag of this format with the right channel.
- Update firebase remote config as needed.
