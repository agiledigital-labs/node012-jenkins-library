def call() {
	return [
		containerTemplate(
			name: 'build-grunt-node012',
			image: 'agiledigital/build-image-grunt-node012',
	        alwaysPullImage: true,
			command: 'cat',
			ttyEnabled: true
		)
	]
}