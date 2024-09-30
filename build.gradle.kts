import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
	java
	`maven-publish`
	alias(libs.plugins.gap.bundle)
	alias(libs.plugins.gap.pkg)
	alias(libs.plugins.nebula)
	alias(libs.plugins.quality)
	alias(libs.plugins.cpd)
	alias(libs.plugins.node)
}

group = "com.swisscom.aem.tools"

defaultTasks("check")

quality {
	checkstyleVersion = libs.versions.checkstyle.get()
	pmdVersion = libs.versions.pmd.get()
	spotbugsVersion = libs.versions.spotbugs.get()
	spotbugsEffort = "max" // min, less, more or max
	spotbugsLevel = "low" // low, medium, high
}

afterEvaluate {
	tasks.named("check") {
		dependsOn("checkQualityMain")
	}

	dependencies {
		add("spotbugsPlugins", libs.findsecbugs)
		add("spotbugsPlugins", libs.sbContrib)
		add("pmd", libs.pmd.ant)
		add("pmd", libs.pmd.java)

		// Spotbugs runtime annotations
		add("compileOnly", libs.spotbugs.annotations)
	}
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.compilerArgs.add("-Xlint:all")
		options.compilerArgs.add("-Xlint:-processing")
		options.compilerArgs.add("-Xlint:-serial")
		options.compilerArgs.add("-Werror")
	}

	test {
		useJUnitPlatform()
	}

	javadoc {
		options.encoding = "UTF-8"
	}
}

java {
	afterEvaluate {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	dependencies {
		// Lombok
		annotationProcessor(libs.lombok)
		compileOnly(libs.lombok)

		// AEM
		compileOnly(libs.servlet)
		compileOnly(libs.gson)
		compileOnly(libs.apacheCommons.lang3)
		compileOnly(libs.osgi.cmpn)
		compileOnly(libs.jcr)
		compileOnly(libs.spiCommons)
		compileOnly(libs.sling.api)
		compileOnly(libs.sling.annotations)

		// Test framework
		testImplementation(libs.junit)
		testImplementation(libs.aemMock)
		testImplementation(libs.oakMock)
		testImplementation(libs.mockito)
		testImplementation(libs.aemUberJar)
	}
}

node {
	version.set(libs.versions.node.get())
	npmVersion.set(libs.versions.npm.get())
	npmInstallCommand.set("ci")
	download.set(true)
}

aem {
	bundleEmbed(libs.apacheCommons.jexl, "org.apache.commons.jexl3.*", export = false)

	tasks {
		packageCompose {

		}

		withType<Jar> {
			bundle {
				javaPackage.set("${project.group}.jcrhopper")
			}
		}
	}
}
