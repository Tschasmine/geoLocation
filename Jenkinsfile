node {
	try {
		stage('Setup') {
			checkout scm
			prepareEnv()
			currentBuild.description = "Setup"
			initGradleProperties()
			startDockerContainers()
		}
		stage('Build') {
			currentBuild.description = "Build"
			sh './gradlew checkPreconditions'
			sh './gradlew fullInstall'
		}
		stage('Test') {
			currentBuild.description = "Test"
			sh './gradlew verify'
			sh './gradlew createAppJar'
		}
	} catch (any) {
		currentBuild.description = currentBuild.description + " failed"
		any.printStackTrace()
		currentBuild.result = 'FAILURE'
		throw any
	} finally {
		stopDockerContainers()

		junit allowEmptyResults: true, testResults: 'build/test-results/**/*.xml'
		archiveArtifacts 'build/reports/**'
		archiveArtifacts 'build/libs/*.jar'
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

def initGradleProperties() {
	sh './initGradleProperties.sh'
	withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
			passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
		sh '''
			echo esdkSnapshotURL=https://registry.abas.sh/repository/abas.esdk.snapshots/ >> gradle.properties
			echo esdkReleaseURL=https://registry.abas.sh/repository/abas.esdk.releases/ >> gradle.properties
			echo nexusUser=$MAVENUSER >> gradle.properties
			echo nexusPassword=$MAVENPASSWORD >> gradle.properties
			echo PARTNER_USER=$MAVENUSER >> gradle.properties
			echo PARTNER_PASSWORD=$MAVENPASSWORD >> gradle.properties
		'''
	}
}

def startDockerContainers() {
	withCredentials([usernamePassword(credentialsId: '82305355-11d8-400f-93ce-a33beb534089',
			passwordVariable: 'MAVENPASSWORD', usernameVariable: 'MAVENUSER')]) {
		sh 'docker login partner.registry.abas.sh -u $MAVENUSER -p $MAVENPASSWORD'
	}
	sh 'docker-compose up -d'
	sleep 30
}

def stopDockerContainers() {
	sh 'docker-compose down || true'
}
