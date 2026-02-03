# gitlab-checks

## Introduction

This plugin implements [Checks API](https://plugins.jenkins.io/checks-api/) for GitLab.

## Getting started

1. Configure project or group to use [GitLab Branch Source](https://plugins.jenkins.io/gitlab-branch-source/) plugin
2. Install this plugin
3. All steps from Checks API will now work.

## Features

| Jenkins Checks API feature                                       | GitLab feature                                        |
|------------------------------------------------------------------|-------------------------------------------------------|
| Name (`ChecksDetails.getName()`)                                 | Commit status name                                    |
| Details URL (`ChecksDetails.getDetailsURL()`)                    | Target URL of commit status                           |
| Started/completed date                                           | Started/finished date                                 |                                                       
| Output summary (`ChecksDetails.getOutput().getSummary()`)        | Commit status description                             |
| Output title (`ChecksDetails.getOutput().getTitle()`)            | Commit status status                                  |
| Output text (`ChecksDetails.getOutput().getText()`)              | Skipped (no matching feature in GitLab commit status) |
| Annotations (`ChecksDetails.getOutput().getChecksAnnotations()`) | Not supported                                         |
| Images (`ChecksDetails.getOutput().getChecksImages()`)           | Not supported                                         |

## Issues

Report issues and enhancements in the GitHub issue tracker.

## Contributing

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

