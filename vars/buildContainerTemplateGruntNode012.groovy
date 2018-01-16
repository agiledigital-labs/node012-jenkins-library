def call() {
	return containerTemplate(
		name: 'build-grunt-node012',
		image: 'agiledigital/build-image-grunt-node012',
		command: 'cat',
		ttyEnabled: true
	)
}