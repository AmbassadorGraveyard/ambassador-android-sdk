# Ambassador Android SDK

[![Codacy Badge](https://api.codacy.com/project/badge/grade/c20d0e4a62674af38c6caef27cdf1c39)](https://www.codacy.com) [![Codacy Badge](https://api.codacy.com/project/badge/coverage/c20d0e4a62674af38c6caef27cdf1c39)](https://www.codacy.com)

_**Support Level**_: API 16+ (4.1+)

## Getting Started

Install Git hooks:

```sh
$ ln -s ../../git-hooks/prepare-commit-msg .git/hooks/prepare-commit-msg
$ ln -s ../../git-hooks/pre-push .git/hooks/pre-push
```

The `pre-push` hook requires re-initialization of the repo:

```sh
$ git init
```

Make sure the `pre-push` hook is executable:

```sh
$ chmod +x .git/hooks/pre-push
```

## Releasing the SDK

In the ambassadorsdk module's build.gradle, inside of the ext block at the bottom, increment libraryVersion. Make sure that `IS_RELEASE_BUILD` is true.

Open both IdentifyApi.java and ConversionsApi.java and find the line ```this.source = "android_sdk_x_x_x";```. Change the version number here as well (the plan
is to make this dynamic in the near future).

Inside the base folder of the project, execute the following command. The command will fail but that is expected :facepalm:.

```sh
./gradlew bintrayUpload
```

Now change directory to your local maven repo.

```sh
cd ~/.m2/repository/com/ambassador/ambassadorsdk
```

Copy your new version somewhere useful like the desktop:

```sh
cp -r 1.x.x ~/Desktop/1.x.x
```

Log in to myget.com and go to https://www.myget.org/feed/Packages/ambassador. Click Add package -> Maven package.

Choose the "Upload one package" radio button. Upload both your .pom and .aar from the Desktop directory above (.jar files are not uploaded). Click Add.

Create a release in Github.

Update the readme.io docs with the new version (located in three places on the page)


## Releasing the Demo App

In the ambassadorsdk-demo module's build.gradle, increment versionCode.

In the ambassadorsdk module's build.gradle, make sure that `IS_RELEASE_MODE` is true.

Make sure you have the demo app keystore downloaded from Ambassador's google drive.

Run `Build->Generate Signed APK...`

Select the ambassadorsdk-demo module and click next.

Navigate to and choose the downloaded keystore for the key store path. The rest of the credentials are in a .txt file next to the keystore file in google drive.

Ensure build type is `Release` and then click build.
