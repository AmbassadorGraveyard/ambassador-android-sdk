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

In the ambassadorsdk module's build.gradle, inside of the ext block at the bottom, increment libraryVersion.

Inside the base folder of the project, execute the following command. The command will fail but that is expected.

```sh
./gradlew bintrayUpload
```

Now change directory to your local maven repo.

```sh
cd ~/.m2/repository/com/ambassador/ambassadorsdk
```

Copy your new version somewhere useful like the desktop. Using version 1.1.5 as an example:

```sh
cp -r 1.1.5 ~/Desktop/1.1.5
```

Login to bintray.com and select the Ambassador organization. Select the maven repo. Select the ambassadorsdk package.

Click "New version". Enter your version number for the name, (ex: "1.1.5"). Click submit. Now click the new version. On the right click to upload files using the UI uploader.

Select the 4 files from the folder you copied to your desktop. Set the path like this:

```
com/ambassador/ambassadorsdk/{VERSION}
```


