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
