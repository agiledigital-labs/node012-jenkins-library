def call(Map config) {
  return [
    [
      path: '/home/jenkins/.npm',
      claimName: "${config.project}-home-jenkins-npm",
      sizeGiB: 1
    ],
    [
      path: '/home/jenkins/.cache/bower',
      claimName: "${config.project}-home-jenkins-cache-bower",
      sizeGiB: 1
    ]
  ]
}