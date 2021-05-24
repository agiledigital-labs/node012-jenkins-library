def call() {
	return [
		containerTemplate(
			name: 'node012-builder',
			image: 'adelabs/node012-builder',
	        	alwaysPullImage: false,
			command: 'cat',
			ttyEnabled: true
		)
	]
}
