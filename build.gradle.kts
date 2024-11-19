import com.cognifide.gradle.aem.bundle.tasks.bundle
import com.github.gradle.node.npm.task.NpmTask

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
description = "JCR Hopper: Migrate AEM with Grace"

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
		compileOnly(libs.osgi.annotation)
		compileOnly(libs.osgi.cmpn)
		compileOnly(libs.jcr)
		compileOnly(libs.geronimo)
		compileOnly(libs.spiCommons)
		compileOnly(libs.sling.annotations)
		compileOnly(libs.sling.api)
		compileOnly(libs.sling.models)

		// Test framework
		testImplementation(libs.junit)
		testImplementation(libs.aemMock)
		testImplementation(libs.oakMock)
		testImplementation(libs.mockito)
		testImplementation(libs.aemUberJar)
		testImplementation(libs.jsonAssert)
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
		val frontendBuild by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "build"))

			inputs.dir("src/main/frontend")
			inputs.file("package-lock.json")
			outputs.dir(layout.buildDirectory.dir("frontend"))
		}

		val lint by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "test:lint"))

			inputs.dir("src/main/frontend")
			inputs.file("package-lock.json")
			outputs.upToDateWhen { false }
		}

		val tsc by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "test:compile"))

			inputs.dir("src/main/frontend")
			inputs.file("package-lock.json")
		}

		val playwrightInstall by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "install:playwright"))
			outputs.upToDateWhen { false }
		}

		val playwright by registering(NpmTask::class) {
			mustRunAfter(playwrightInstall)
			dependsOn(npmInstall)
			dependsOn(frontendBuild)
			npmCommand.set(listOf("run", "test:playwright"))

			inputs.dir("src/test/playwright")
			inputs.file("configs/playwright.config.ts")
			outputs.dir(project.layout.buildDirectory.dir(name))
			outputs.dir(project.layout.buildDirectory.dir("reports/$name"))
		}

		val format by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "test:format"))

			inputs.dir("src")
			inputs.file("package-lock.json")
			inputs.file(".prettierrc.json")
		}

		val prettierFormat by registering(NpmTask::class) {
			dependsOn(npmInstall)
			npmCommand.set(listOf("run", "test:format", "--", "--write"))
			outputs.upToDateWhen { false }
		}

		packageValidate {
			enabled = true
		}

		check {
			dependsOn(lint, tsc, format, packageValidate, playwright)
		}

		val aemContent by registering(Sync::class) {
			from("$projectDir/src/main/content")
			from(frontendBuild)

			destinationDir = project.layout.buildDirectory.dir(name).get().asFile
			packageOptions.contentDir.set(destinationDir)
		}

		assemble {
			dependsOn(aemContent)
		}

		packagePrepare {
			dependsOn(aemContent)
		}

		withType<Jar> {
			bundle {
				javaPackage.set("${project.group}.jcrhopper")

				bnd(
					"""
					Import-Package: javax.annotation;version=0.0.0,\
						org.apache.commons.logging;version=0.0.0,\
						*;provide:=false
				""".trimIndent()
				)
			}
		}
	}
}

publishing {
	repositories {
		val mavenPublishUser: String? by project
		val mavenPublishPassword: String? by project
		if (mavenPublishUser != null && mavenPublishPassword != null) {
			maven {
				name = "GitHub"
				url = uri("https://maven.pkg.github.com/swisscom/JCR-Hopper")
				credentials {
					username = mavenPublishUser
					password = mavenPublishPassword
				}
			}
		}
	}

	publications {
		create<MavenPublication>("hopper") {
			artifact(tasks.packageCompose)
			from(components["java"])
		}
	}
}
