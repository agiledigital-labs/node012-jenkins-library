def call() {
	return [
		containerTemplate(
			name: 'node012-builder',
			image: 'agiledigital/node012-builder',
	        alwaysPullImage: true,
			command: 'cat',
			ttyEnabled: true
		)
	]
}