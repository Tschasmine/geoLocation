node {
	try {
		stage('Setup') {
			checkout scm
			prepareEnv()
			currentBuild.description = "Setup"
		}
		stage('Build') {
			currentBuild.description = "Build"
		}
		stage('Test') {
			currentBuild.description = "Test"
		}
	} catch (any) {
		any.printStackTrace()
		currentBuild.result = 'FAILURE'
		throw any
	}
}

def prepareEnv() {
	def jdkHome = tool name: 'java8', type: 'jdk'
	env.PATH = "${jdkHome}/bin:${env.PATH}"
	env.JAVA_HOME = "${jdkHome}"
	env.GRADLE_USER_HOME = "${WORKSPACE}/gradle-home"
	sh """
		mkdir -p ${env.GRADLE_USER_HOME}
		echo 'org.gradle.java.home=${env.JAVA_HOME}' > ${env.GRADLE_USER_HOME}/gradle.properties
	"""
	jdkHome = null
}
