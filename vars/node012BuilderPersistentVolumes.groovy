def call() {
  return [
    [path: '/home/jenkins/.npm',         sizeGiB: 1],
    [path: '/home/jenkins/.cache/bower', sizeGiB: 1]
  ].collect { volume ->
    volume.name = "${config.project}-${path.replaceAll(/[^a-zA-Z0-9]+/, '-')}"
    volume
  }
}